package com.cinefms.dbstore.utils.mongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.mongojack.DBCursor;
import org.mongojack.DBQuery.Query;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import com.cinefms.dbstore.api.DBStoreBinary;
import com.cinefms.dbstore.api.DBStoreEntity;
import com.cinefms.dbstore.api.DBStoreQuery;
import com.cinefms.dbstore.api.DataStore;
import com.cinefms.dbstore.api.exceptions.DatabaseException;
import com.cinefms.dbstore.api.exceptions.EntityNotFoundException;
import com.cinefms.dbstore.cache.api.DBStoreCache;
import com.cinefms.dbstore.cache.api.DBStoreCacheFactory;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class MongoDataStore implements DataStore {

	private static Log log = LogFactory.getLog(MongoDataStore.class);

	private MongoService mongoService;

	private DBStoreCacheFactory cacheFactory;
	
	private boolean cacheQueries = false;
	private boolean cacheObjects = false;
	
	private Map<String, JacksonDBCollection<?, String>> collections = new HashMap<String, JacksonDBCollection<?, String>>();
	private Map<String, GridFS> buckets = new HashMap<String, GridFS>();

	private QueryMongojackTranslator fqtl = new QueryMongojackTranslator();

	private <T> JacksonDBCollection<T, String> getCollection(Class<T> clazz) {
		try {
			@SuppressWarnings("unchecked")
			JacksonDBCollection<T, String> out = (JacksonDBCollection<T, String>) collections.get(clazz.getCanonicalName());
			if (out == null) {
				DBCollection dbc = getMongoService().getDb().getCollection(clazz.getCanonicalName());
				out = JacksonDBCollection.wrap(dbc, clazz, String.class);
				collections.put(clazz.getCanonicalName(), out);
			}
			return out;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private GridFS getBucket(String bucket) throws UnknownHostException {
		GridFS out = buckets.get(bucket);
		if (out == null) {
			out = new GridFS(getMongoService().getDb(), bucket);
			buckets.put(bucket, out);
		}
		return out;
	}

	
	public void storeBinary(String bucket, DBStoreBinary binary) throws DatabaseException {
		try {
			GridFS gfs = getBucket(bucket);
			gfs.remove(binary.getId());
			GridFSInputFile f = gfs.createFile(binary.getInputStream());
			DBObject md = new BasicDBObject();
			if (binary.getMetaData() != null) {
				for (Map.Entry<String, Object> e : binary.getMetaData().entrySet()) {
					md.put(e.getKey(), e.getValue());
				}
			}
			f.setMetaData(md);
			f.setFilename(binary.getId());
			f.save();
		} catch (Exception e) {
			throw new DatabaseException(e);
		}
	}

	
	public DBStoreBinary getBinary(String bucket, String filename) throws DatabaseException {
		try {
			GridFS gfs = getBucket(bucket);
			GridFSDBFile f = gfs.findOne(filename);
			if (f == null) {
				return null;
			}
			return new MongoFSBinary(f);
		} catch (Exception e) {
			throw new DatabaseException(e);
		}
	}

	
	public void deleteBinary(String bucket, String id) throws DatabaseException {
		try {
			GridFS gfs = getBucket(bucket);
			gfs.remove(id);
		} catch (Exception e) {
			throw new DatabaseException(e);
		}
	}

	
	public <T extends DBStoreEntity> T findObject(Class<T> clazz, DBStoreQuery query) {
		
		
		String key = query.toString();
		List<String> ids = null;
		DBStoreCache cache = getQueryCache(clazz); 
		if(cache != null) {
			ids = cache.getList(key, String.class);
		}
		if (ids == null) {
			Query q = fqtl.translate(query);
			DBObject o = fqtl.translateOrderBy(query);
			ids = new ArrayList<String>();
			List<T> ts = getCollection(clazz).find(q, new BasicDBObject("id", null)).sort(o).limit(1).toArray();
			if (ts != null) {
				ids = new ArrayList<String>();
				for (T t : ts) {
					ids.add(t.getId());
				}
				if(cache!=null) {
					cache.put(key, ids);
				}
			}
		}
		
		
		if (ids.size() > 0) {
			return getObject(clazz, ids.get(0));
		}
		return null;
	}

	
	public <T extends DBStoreEntity> List<T> findObjects(Class<T> clazz, DBStoreQuery query) throws EntityNotFoundException {
		
		
		String key = query.toString();
		List<String> ids = null;
		DBStoreCache cache = getQueryCache(clazz); 
		if(cache !=null) {
			ids = cache.getList(key, String.class);
		}
		if (ids == null) {
			log.debug("getting from datastore (not in cache ...)");
			Query q = fqtl.translate(query);
			DBObject o = fqtl.translateOrderBy(query);
			ids = new ArrayList<String>();
			int skip = query.getStart();
			int max = query.getMax();

			log.debug(" ---> LIMIT (" + skip + ":" + max + ")");

			DBCursor<T> c = getCollection(clazz).find(q,
					new BasicDBObject("id", null));

			if (o != null) {
				c = c.sort(o);
			}
			if (skip != 0) {
				c = c.skip(skip);
			}
			if (max != -1) {
				c = c.limit(max);
			}
			List<T> x = c.toArray();
			log.debug("-- db query: found " + x.size() + " matches for query ("
					+ clazz.getCanonicalName() + ":" + query.toString() + ")");
			for (T t : x) {
				ids.add(t.getId());
			}
			
			if(cache!=null) {
				cache.put(key, ids);
			}

		} else {
			log.debug("getting from datastore (in cache ...)");
			log.debug("-- cached: found " + ids.size() + " results for query: "+ query.toString());
		}
		List<T> out = new ArrayList<T>();
		for (String s : ids) {
			T t = getObject(clazz, s);
			if (t != null) {
				out.add(t);
			}
		}

		log.debug("returning " + out.size() + " matches for query ("+ clazz.getCanonicalName() + ":" + query.toString() + ")");
		
		
		return out;
	}

	
	public <T extends DBStoreEntity> T getObject(Class<T> clazz, String id) {
		
		
		DBStoreCache cache = getObjectCache(clazz);
		T out = null;
		if(cache!=null) {
			out = cache.get(id, clazz);
			log.debug(" result from cache: "+out);
		}
		if (out == null) {
			log.debug(" result from cache [NULL], checking DB ... ");
			out = getCollection(clazz).findOneById(id);
			if (out != null && cache != null) {
				cache.put(id,out);
			}
		}
		
		return out;
	}
	

	
	public <T extends DBStoreEntity> boolean deleteObject(T object) throws DatabaseException {
		if(object!=null && object.getId()!=null) {
			return deleteObject(object.getClass(),object.getId());
		}
		return false;
	}

	
	public <T extends DBStoreEntity> boolean deleteObject(Class<T> clazz, String id) throws DatabaseException {
		
		try {
			getCollection(clazz).removeById(id);
			DBStoreCache objectCache = getObjectCache(clazz);
			if(objectCache!=null) {
				objectCache.remove(id);
			}
			DBStoreCache queryCache = getQueryCache(clazz);
			if(queryCache!=null) {
				queryCache.removeAll();
			}
			
			return true;
		} catch (Exception e) {
			throw new DatabaseException(e);
		}
	}

	
	@SuppressWarnings("unchecked")
	public <T extends DBStoreEntity> T saveObject(T object) throws EntityNotFoundException {
		
		
		JacksonDBCollection<T, String> coll = (JacksonDBCollection<T, String>) getCollection(object.getClass());
		if (object.getId() == null) {
			object.setId(ObjectId.get().toString());
		}
		WriteResult<T, String> wr = coll.save(object);
		String id = wr.getSavedId();
		T out = (T) getObject(object.getClass(), id);
		
		DBStoreCache objectCache = getObjectCache(object.getClass());
		if(objectCache!=null) {
			log.debug("updating object cache for: "+id);
			objectCache.remove(out.getId());
			//objectCache.put(out.getId(), out);
		}
		DBStoreCache queryCache = getQueryCache(object.getClass());
		if(queryCache!=null) {
			queryCache.removeAll();
		}
		
		
		return (T)getObject(object.getClass(), object.getId());
	}

	
	private DBStoreCache getObjectCache(Class<? extends DBStoreEntity> clazz) {
		if(cacheFactory!=null && cacheObjects) {
			return cacheFactory.getCache(clazz.getCanonicalName()+":object");
		}
		return null;
	}
	
	private DBStoreCache getQueryCache(Class<? extends DBStoreEntity> clazz) {
		if(cacheFactory!=null && cacheQueries) {
			return cacheFactory.getCache(clazz.getCanonicalName()+":query");
		}
		return null;
	}

	public MongoService getMongoService() {
		return mongoService;
	}

	public void setMongoService(MongoService mongoService) {
		this.mongoService = mongoService;
	}

	public DBStoreCacheFactory getDBStoreCacheFactory() {
		return cacheFactory;
	}

	public void setDBStoreCacheFactory(DBStoreCacheFactory cacheFactory) {
		this.cacheFactory = cacheFactory;
	}

	public boolean isCacheQueries() {
		return cacheQueries;
	}

	public void setCacheQueries(boolean cacheQueries) {
		this.cacheQueries = cacheQueries;
	}

	public boolean isCacheObjects() {
		return cacheObjects;
	}

	public void setCacheObjects(boolean cacheObjects) {
		this.cacheObjects = cacheObjects;
	}

	
	
	

}
