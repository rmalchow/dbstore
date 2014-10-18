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

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.skjlls.utils.cache.SkjllsCache;
import com.skjlls.utils.cache.SkjllsCacheFactory;
import com.skjlls.utils.db.SkjllsBinary;
import com.skjlls.utils.db.SkjllsDataStore;
import com.skjlls.utils.db.SkjllsEntity;
import com.skjlls.utils.db.SkjllsQuery;
import com.skjlls.utils.db.exceptions.DatabaseException;
import com.skjlls.utils.db.exceptions.EntityNotFoundException;
import com.skjlls.utils.metrics.MetricUtil;

public class MongoDataStore implements SkjllsDataStore {

	private static Log log = LogFactory.getLog(MongoDataStore.class);

	private MongoService mongoService;

	private SkjllsCacheFactory cacheFactory;
	
	private boolean cacheQueries = false;
	private boolean cacheObjects = false;
	
	private Map<String, JacksonDBCollection<?, String>> collections = new HashMap<String, JacksonDBCollection<?, String>>();
	private Map<String, GridFS> buckets = new HashMap<String, GridFS>();

	private SkjllsQueryMongojackTranslator fqtl = new SkjllsQueryMongojackTranslator();

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

	@Override
	public void storeBinary(String bucket, SkjllsBinary binary) throws DatabaseException {
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

	@Override
	public SkjllsBinary getBinary(String bucket, String filename) throws DatabaseException {
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

	@Override
	public void deleteBinary(String bucket, String id) throws DatabaseException {
		try {
			GridFS gfs = getBucket(bucket);
			gfs.remove(id);
		} catch (Exception e) {
			throw new DatabaseException(e);
		}
	}

	@Override
	public <T extends SkjllsEntity> T findObject(Class<T> clazz, SkjllsQuery query) {
		
		MetricUtil.start("mongo_db.findobject."+clazz.getSimpleName());
		String key = query.toString();
		List<String> ids = null;
		SkjllsCache cache = getQueryCache(clazz); 
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
		
		MetricUtil.end("mongo_db.findobject."+clazz.getSimpleName());
		if (ids.size() > 0) {
			return getObject(clazz, ids.get(0));
		}
		return null;
	}

	@Override
	public <T extends SkjllsEntity> List<T> findObjects(Class<T> clazz, SkjllsQuery query) throws EntityNotFoundException {
		
		MetricUtil.start("mongo_db.findobjects."+clazz.getSimpleName());
		String key = query.toString();
		List<String> ids = null;
		SkjllsCache cache = getQueryCache(clazz); 
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
		
		MetricUtil.end("mongo_db.findobjects."+clazz.getSimpleName());
		return out;
	}

	@Override
	public <T extends SkjllsEntity> T getObject(Class<T> clazz, String id) {
		
		MetricUtil.start("mongo_db.getobject."+clazz.getSimpleName());
		SkjllsCache cache = getObjectCache(clazz);
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
		MetricUtil.end("mongo_db.getobject."+clazz.getSimpleName());
		return out;
	}
	

	@Override
	public <T extends SkjllsEntity> boolean deleteObject(T object) throws DatabaseException {
		if(object!=null && object.getId()!=null) {
			return deleteObject(object.getClass(),object.getId());
		}
		return false;
	}

	@Override
	public <T extends SkjllsEntity> boolean deleteObject(Class<T> clazz, String id) throws DatabaseException {
		
		try {
			getCollection(clazz).removeById(id);
			SkjllsCache objectCache = getObjectCache(clazz);
			if(objectCache!=null) {
				objectCache.remove(id);
			}
			SkjllsCache queryCache = getQueryCache(clazz);
			if(queryCache!=null) {
				queryCache.removeAll();
			}
			
			return true;
		} catch (Exception e) {
			throw new DatabaseException(e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends SkjllsEntity> T saveObject(T object) throws EntityNotFoundException {
		MetricUtil.start("mongo_db.saveobjects."+object.getClass().getName());
		
		JacksonDBCollection<T, String> coll = (JacksonDBCollection<T, String>) getCollection(object.getClass());
		if (object.getId() == null) {
			object.setId(ObjectId.get().toString());
		}
		WriteResult<T, String> wr = coll.save(object);
		String id = wr.getSavedId();
		T out = (T) getObject(object.getClass(), id);
		
		SkjllsCache objectCache = getObjectCache(object.getClass());
		if(objectCache!=null) {
			log.debug("updating object cache for: "+id);
			objectCache.remove(out.getId());
			//objectCache.put(out.getId(), out);
		}
		SkjllsCache queryCache = getQueryCache(object.getClass());
		if(queryCache!=null) {
			queryCache.removeAll();
		}
		
		MetricUtil.end("mongo_db.saveobjects."+object.getClass().getName());
		return (T)getObject(object.getClass(), object.getId());
	}

	
	private SkjllsCache getObjectCache(Class<? extends SkjllsEntity> clazz) {
		if(cacheFactory!=null && cacheObjects) {
			return cacheFactory.getCache(clazz.getCanonicalName()+":object");
		}
		return null;
	}
	
	private SkjllsCache getQueryCache(Class<? extends SkjllsEntity> clazz) {
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

	public SkjllsCacheFactory getCacheFactory() {
		return cacheFactory;
	}

	public void setCacheFactory(SkjllsCacheFactory cacheFactory) {
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
