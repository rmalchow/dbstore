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
import com.cinefms.dbstore.api.DBStoreListener;
import com.cinefms.dbstore.api.DBStoreQuery;
import com.cinefms.dbstore.api.DataStore;
import com.cinefms.dbstore.api.exceptions.DBStoreException;
import com.cinefms.dbstore.api.exceptions.EntityNotFoundException;
import com.cinefms.dbstore.cache.api.DBStoreCache;
import com.cinefms.dbstore.cache.api.DBStoreCacheFactory;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class MongoDataStore implements DataStore {

	private static Log log = LogFactory.getLog(MongoDataStore.class);

	private MongoService mongoService;
	private DBStoreCacheFactory cacheFactory;
	
	private String defaultDb;
	private String dbPrefix;
	
	private boolean cacheQueries = false;
	private boolean cacheObjects = false;
	
	private Map<String, JacksonDBCollection<?, String>> collections = new HashMap<String, JacksonDBCollection<?, String>>();
	private Map<String, List<DBStoreListener>> listenerMap = new HashMap<String, List<DBStoreListener>>();
	private Map<String, GridFS> buckets = new HashMap<String, GridFS>();

	private QueryMongojackTranslator fqtl = new QueryMongojackTranslator();

	private List<DBStoreListener> listeners = new ArrayList<DBStoreListener>(); 
	
	private DB getDB(String db) throws UnknownHostException {
		db = db==null?defaultDb:dbPrefix+"_"+db;
		return getMongoService().getDb(db);
	}
	
	private <T> JacksonDBCollection<T, String> getCollection(String db, Class<T> clazz) {
		try {
			String key = db+":"+clazz.getCanonicalName();
			@SuppressWarnings("unchecked")
			JacksonDBCollection<T, String> out = (JacksonDBCollection<T, String>) collections.get(key);
			if (out == null) {
				DBCollection dbc = null;
				getDB(db).getCollection(clazz.getCanonicalName());
				out = JacksonDBCollection.wrap(dbc, clazz, String.class);
				collections.put(key, out);
			}
			return out;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private GridFS getBucket(String db, String bucket) throws UnknownHostException {
		GridFS out = buckets.get(bucket);
		if (out == null) {
			out = new GridFS(getDB(db), bucket);
			buckets.put(bucket, out);
		}
		return out;
	}

	private List<DBStoreListener> getListeners(Class<? extends DBStoreEntity> clazz) {
		List<DBStoreListener> out = listenerMap.get(clazz.getCanonicalName());
		if(out == null) {
			List<DBStoreListener> lx = new ArrayList<DBStoreListener>();
			for(DBStoreListener l : listeners) {
				if(l.supports(clazz)) {
					lx.add(l);
				}
			}
			out = new ArrayList<DBStoreListener>(lx);
		}
		return out;
	}
	
	public void storeBinary(String db, String bucket, DBStoreBinary binary) throws DBStoreException {
		try {
			GridFS gfs = getBucket(db,bucket);
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
			throw new DBStoreException(e);
		}
	}

	
	public DBStoreBinary getBinary(String db, String bucket, String filename) throws DBStoreException {
		try {
			GridFS gfs = getBucket(db,bucket);
			GridFSDBFile f = gfs.findOne(filename);
			if (f == null) {
				return null;
			}
			return new MongoFSBinary(f);
		} catch (Exception e) {
			throw new DBStoreException(e);
		}
	}

	
	public void deleteBinary(String db, String bucket, String id) throws DBStoreException {
		try {
			GridFS gfs = getBucket(db,bucket);
			gfs.remove(id);
		} catch (Exception e) {
			throw new DBStoreException(e);
		}
	}

	
	public <T extends DBStoreEntity> T findObject(String db, Class<T> clazz, DBStoreQuery query) {
		
		String key = query.toString();
		List<String> ids = null;
		DBStoreCache cache = getQueryCache(db,clazz); 
		if(cache != null) {
			ids = cache.getList(key, String.class);
		}
		if (ids == null) {
			Query q = fqtl.translate(query);
			DBObject o = fqtl.translateOrderBy(query);
			ids = new ArrayList<String>();
			List<T> ts = getCollection(db,clazz).find(q, new BasicDBObject("id", null)).sort(o).limit(1).toArray();
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
			return getObject(db, clazz, ids.get(0));
		}
		return null;
	}

	
	public <T extends DBStoreEntity> List<T> findObjects(String db, Class<T> clazz, DBStoreQuery query) throws EntityNotFoundException {
		
		String key = query.toString();
		List<String> ids = null;
		DBStoreCache cache = getQueryCache(db,clazz); 
		
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

			DBCursor<T> c = getCollection(db,clazz).find(q,new BasicDBObject("id", null));

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
			log.debug("-- db query: found " + x.size() + " matches for query ("+ clazz.getCanonicalName() + ":" + query.toString() + ")");
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
			T t = getObject(db, clazz, s);
			if (t != null) {
				out.add(t);
			}
		}

		log.debug("returning " + out.size() + " matches for query ("+ clazz.getCanonicalName() + ":" + query.toString() + ")");
		
		
		return out;
	}

	
	public <T extends DBStoreEntity> T getObject(String db, Class<T> clazz, String id) {
		
		DBStoreCache cache = getObjectCache(db,clazz);
		T out = null;
		if(cache!=null) {
			out = cache.get(id, clazz);
			log.debug(" result from cache: "+out);
		}
		if (out == null) {
			log.debug(" result from cache [NULL], checking DB ... ");
			out = getCollection(db,clazz).findOneById(id);
			if (out != null && cache != null) {
				cache.put(id,out);
			}
		}
		
		return out;
	}
	

	
	public <T extends DBStoreEntity> boolean deleteObject(String db, T object) throws DBStoreException {
		if(object!=null && object.getId()!=null) {
			return deleteObject(db,object.getClass(), object.getId());
		}
		return false;
	}

	
	public <T extends DBStoreEntity> boolean deleteObject(String db, Class<T> clazz, String id) throws DBStoreException {
		
		DBStoreEntity entity = getObject(db, clazz, id);
		if(id==null) {
			return false;
		}
		
		for(DBStoreListener l : getListeners(clazz)) {
			l.beforeDelete(db, entity);
		}
		
		try {
			getCollection(db,clazz).removeById(id);
			DBStoreCache objectCache = getObjectCache(db,clazz);
			if(objectCache!=null) {
				objectCache.remove(id);
			}
			DBStoreCache queryCache = getQueryCache(db,clazz);
			if(queryCache!=null) {
				queryCache.removeAll();
			}
			for(DBStoreListener l : getListeners(clazz)) {
				l.deleted(db, entity);
			}
			return true;
		} catch (Exception e) {
			throw new DBStoreException(e);
		}
	}

	
	@SuppressWarnings("unchecked")
	public <T extends DBStoreEntity> T saveObject(String db, T object) throws EntityNotFoundException {
		
		for(DBStoreListener l : getListeners(object.getClass())) {
			l.beforeSave(db, object);
		}
		
		JacksonDBCollection<T, String> coll = (JacksonDBCollection<T, String>) getCollection(db,object.getClass());

		if (object.getId() == null) {
			object.setId(ObjectId.get().toString());
		}
		
		WriteResult<T, String> wr = coll.save(object);
		String id = wr.getSavedId();
		T out = (T) getObject(db, object.getClass(), id);
		
		DBStoreCache objectCache = getObjectCache(db,object.getClass());
		if(objectCache!=null) {
			log.debug("updating object cache for: "+id);
			objectCache.remove(out.getId());
		}

		DBStoreCache queryCache = getQueryCache(db,object.getClass());
		if(queryCache!=null) {
			queryCache.removeAll();
		}
		
		out = (T)getObject(db, object.getClass(), object.getId());
		for(DBStoreListener l : getListeners(object.getClass())) {
			l.saved(db, out);
		}
		return out; 
	}

	/**
	private DBStoreCache getObjectCache(Class<? extends DBStoreEntity> clazz) {
		return getObjectCache("", clazz);
	}
	
	private DBStoreCache getQueryCache(Class<? extends DBStoreEntity> clazz) {
		return getQueryCache("", clazz);
	}
	**/

	private DBStoreCache getObjectCache(String db, Class<? extends DBStoreEntity> clazz) {
		if(getCacheFactory()!=null && cacheObjects) {
			return getCacheFactory().getCache(db+":"+clazz.getCanonicalName()+":object");
		}
		return null;
	}
	
	private DBStoreCache getQueryCache(String db, Class<? extends DBStoreEntity> clazz) {
		if(getCacheFactory()!=null && cacheQueries) {
			return getCacheFactory().getCache(db+":"+clazz.getCanonicalName()+":query");
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
		return getCacheFactory();
	}

	public void setDBStoreCacheFactory(DBStoreCacheFactory cacheFactory) {
		this.setCacheFactory(cacheFactory);
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

	
	public void addListener(DBStoreListener listener) {
		this.listeners.add(listener);
	}

	public DBStoreCacheFactory getCacheFactory() {
		return cacheFactory;
	}

	public void setCacheFactory(DBStoreCacheFactory cacheFactory) {
		this.cacheFactory = cacheFactory;
	}

	public String getDefaultDb() {
		return defaultDb;
	}

	public void setDefaultDb(String defaultDb) {
		this.defaultDb = defaultDb;
	}

	public String getDbPrefix() {
		return dbPrefix;
	}

	public void setDbPrefix(String dbPrefix) {
		this.dbPrefix = dbPrefix;
	}
	

}
