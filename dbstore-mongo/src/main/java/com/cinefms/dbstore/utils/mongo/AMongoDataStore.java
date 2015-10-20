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
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.JacksonDBCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.cinefms.dbstore.api.DBStoreBinary;
import com.cinefms.dbstore.api.DBStoreEntity;
import com.cinefms.dbstore.api.DBStoreListener;
import com.cinefms.dbstore.api.DataStore;
import com.cinefms.dbstore.api.annotations.Index;
import com.cinefms.dbstore.api.annotations.Indexes;
import com.cinefms.dbstore.api.annotations.Write;
import com.cinefms.dbstore.api.annotations.WriteMode;
import com.cinefms.dbstore.api.exceptions.DBStoreException;
import com.cinefms.dbstore.api.exceptions.EntityNotFoundException;
import com.cinefms.dbstore.cache.api.DBStoreCache;
import com.cinefms.dbstore.cache.api.DBStoreCacheFactory;
import com.cinefms.dbstore.query.api.DBStoreQuery;
import com.cinefms.dbstore.query.mongo.QueryMongojackTranslator;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public abstract class AMongoDataStore implements DataStore {

	private static Log log = LogFactory.getLog(AMongoDataStore.class);

	private MongoService mongoService;
	private DBStoreCacheFactory cacheFactory;
	
	private String defaultDb;
	private String dbPrefix;
	
	private boolean cacheQueries = false;
	private boolean cacheObjects = false;
	
	private Map<String,DBStoreCache> objectCaches = new HashMap<String, DBStoreCache>();
	private Map<String,DBStoreCache> queryCaches = new HashMap<String, DBStoreCache>();
	
	private Map<String, JacksonDBCollection<?, String>> collections = new HashMap<String, JacksonDBCollection<?, String>>();
	private Map<String, List<DBStoreListener>> listenerMap = new HashMap<String, List<DBStoreListener>>();
	private Map<String, GridFS> buckets = new HashMap<String, GridFS>();

	private QueryMongojackTranslator fqtl = new QueryMongojackTranslator();

	private List<DBStoreListener> listeners = null; 
	
	@Autowired
	private ApplicationContext ctx;
	
	private DB getDB(String db) throws UnknownHostException {
		db = db==null?defaultDb:(dbPrefix==null?"":(dbPrefix+"_"))+db;
		log.info("============================================================");
		log.info("== ");
		log.info("== ");
		log.info("== getting DB from mongoService: "+getMongoService());
		DB out = getMongoService().getDb(db);
		log.info("== ... result is: "+out);
		log.info("== ");
		log.info("============================================================");
		return out;
	}

	public abstract String getCollectionName(Class<?> clazz);
	
	
	private <T> DBCollection initializeCollection(DB db, Class<T> clazz) {
		String collectionName = getCollectionName(clazz);
		DBCollection dbc = db.getCollection(collectionName);
		if(clazz.getAnnotation(Indexes.class)!=null) {
			for(Index i : clazz.getAnnotation(Indexes.class).value()) {

				BasicDBObjectBuilder idx = BasicDBObjectBuilder.start();
				for(String f : i.fields()) {
					idx = idx.add(f, 1);
				}

				BasicDBObjectBuilder options =  BasicDBObjectBuilder.start();
				if(i.unique()) {
					options.add("unique", true);
				}
				log.info(" === CREATING INDEX: "+idx.get()+" ==== ");
				dbc.createIndex(idx.get(),options.get());
			}
		}
		return dbc;
	}
	
	private <T> JacksonDBCollection<T, String> getCollection(String db, Class<T> clazz) {
		try {
			String collectionName = getCollectionName(clazz); 
			String key = db+":"+collectionName;
			log.debug(" == DB        : "+db);
			log.debug(" == Collection: "+collectionName);
			@SuppressWarnings("unchecked")
			JacksonDBCollection<T, String> out = (JacksonDBCollection<T, String>) collections.get(key);
			if (out == null) {
				log.info("============================================================");
				log.info("==");
				log.info("== DB COLLECTION NOT CREATED .... (creating...) ");
				log.info("==");
				log.info("==  CLAZZ IS: "+clazz.getCanonicalName());
				log.info("== DBNAME IS: "+db);
				DB d = getDB(db);
				log.info("==     DB IS: "+d);
				DBCollection dbc = initializeCollection(d, clazz);
				log.info("==    DBC IS: "+dbc);
				out = JacksonDBCollection.wrap(dbc, clazz, String.class);
				if(clazz.getAnnotation(Write.class)!=null && clazz.getAnnotation(Write.class).value()==WriteMode.FAST) {
					out.setWriteConcern(WriteConcern.UNACKNOWLEDGED);
				}
				collections.put(key, out);
				log.info("==");
				log.info("============================================================");
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
	
	public List<DBStoreListener> getAllListeners() {
		if(listeners == null) {
			if(ctx!=null) {
				listeners = new ArrayList<DBStoreListener>(ctx.getBeansOfType(DBStoreListener.class).values());
			} else {
				listeners = new ArrayList<DBStoreListener>();
			}
			
		}
		return listeners;
	}
	

	private List<DBStoreListener> getListeners(Class<? extends DBStoreEntity> clazz) {
		List<DBStoreListener> out = listenerMap.get(clazz.getCanonicalName());
		if(out == null) {
			List<DBStoreListener> lx = new ArrayList<DBStoreListener>();
			List<DBStoreListener> cl = getAllListeners();
			log.debug("listeners total: "+cl.size());
			for(DBStoreListener l : cl) {
				if(l.supports(clazz)) {
					log.debug("listeners on "+clazz+": "+l.getClass()+" supports");
					lx.add(l);
				} else {
					log.debug("listeners on "+clazz+": "+l.getClass()+" does not support");
				}
			}
			out = new ArrayList<DBStoreListener>(lx);
		}
		log.debug("listeners on "+clazz+": "+out.size());
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
			f.setId(binary.getId());
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

		Query q = fqtl.translate(query);
		DBObject o = fqtl.translateOrderBy(query);

		List<T> ts = getCollection(db,clazz).find(q, new BasicDBObject("id", null)).sort(o).limit(1).toArray();
		
		if (ts != null) {
			log.debug(" --> found "+ts.size()+" elements!");
			for (T t : ts) {
				return t;
			}
		}
		
		return null;
	}

	public <T extends DBStoreEntity> int countObjects(String db, Class<T> clazz, DBStoreQuery query) throws EntityNotFoundException {
		Query q = fqtl.translate(query);
		return getCollection(db,clazz).find(q).count();
	}

	public <T extends DBStoreEntity> List<T> findObjects(String db, Class<T> clazz, DBStoreQuery query) throws EntityNotFoundException {
		
		List<T> out = new ArrayList<T>();
		
		log.debug(" -----> cache miss!");
		log.debug("getting from datastore (not in cache ...)");
		Query q = fqtl.translate(query);
		DBObject o = fqtl.translateOrderBy(query);

		long skip = query.getStart();
		long max = query.getMax();

		log.debug(" ---> LIMIT (" + skip + ":" + max + ")");

		DBCursor<T> c = getCollection(db,clazz).find(q);
		
		if (o != null) {
			c = c.sort(o);
		}
		if (skip != 0) {
			c = c.skip((int)skip);
		}
		if (max != -1) {
			c = c.limit((int)max);
		}
		List<T> x = c.toArray();
		
		log.debug("-- db query: found " + x.size() + " matches for query ("+ clazz.getCanonicalName() + ":" + query.toString() + ")");
		for (T t : x) {
			out.add(t);
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

	@Override
	public <T extends DBStoreEntity> void deleteObjects(String db, Class<T> clazz, DBStoreQuery query) throws DBStoreException {
		Query q = fqtl.translate(query);
		getCollection(db,clazz).remove(q);
	}
	
	
	public <T extends DBStoreEntity> boolean deleteObject(String db, Class<T> clazz, String id) throws DBStoreException {
		
		if(id==null) {
			return false;
		}

		DBStoreEntity entity = getObject(db, clazz, id);
		if(entity==null) {
			return false;
		}
		
		for(DBStoreListener l : getListeners(clazz)) {
			log.debug("firing 'beforeDelete' for: "+clazz+" / "+id);
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
				log.debug("firing 'delete' for: "+clazz+" / "+id);
				l.deleted(db, entity);
			}
			return true;
		} catch (Exception e) {
			throw new DBStoreException(e);
		}
	}

	
	@SuppressWarnings("unchecked")
	public <T extends DBStoreEntity> T saveObject(String db, T object) throws EntityNotFoundException {
		
		
		List<DBStoreListener> listeners = getListeners(object.getClass()); 

		log.debug(object.getClass()+" / saving object: "+object.getId()+", notifying "+listeners.size()+" listeners");

		for(DBStoreListener l : listeners) {
			log.debug("firing 'beforeSave' for: "+object.getClass()+" / "+object.getId());
			l.beforeSave(db, object);
		}
		
		JacksonDBCollection<T, String> coll = (JacksonDBCollection<T, String>) getCollection(db,object.getClass());
		

		T old = null;
		if(object.getId() != null) {
			old = coll.findOneById(object.getId());
		} else {
			object.setId(ObjectId.get().toString());
		}

		if(old!=null) {
			coll.update(DBQuery.is("_id", old.getId()), object);
		} else {
			coll.save(object);
		}
		
		T out = (T) getObject(db, object.getClass(), object.getId());

		if(cacheObjects) {
			DBStoreCache objectCache = getObjectCache(db,object.getClass());
			if(objectCache!=null) {
				objectCache.remove(out.getId());
			}
		}

		if(cacheQueries) {
			DBStoreCache queryCache = getQueryCache(db,object.getClass());
			if(queryCache!=null) {
				queryCache.removeAll();
			}
		}
		
		if(listeners.size()>0) {
			for(DBStoreListener l : listeners) {
				if(old!=null) {
					l.updated(db, old, out);
				} else {
					l.created(db, out);
				}
			}
		}
		return out; 
	}

	private DBStoreCache getObjectCache(String db, Class<? extends DBStoreEntity> clazz) {
		String key = db+":"+clazz.getCanonicalName()+":object";
		DBStoreCache objectCache = objectCaches.get(key);
		if(objectCache==null && getCacheFactory()!=null && cacheObjects) {
			synchronized(this) {
				if(objectCache==null) {
					objectCache = getCacheFactory().getCache(key);
					if(objectCache!=null) {
						objectCaches.put(key,objectCache);
					}
				}
			}
		}
		return objectCache;
	}
	
	private DBStoreCache getQueryCache(String db, Class<? extends DBStoreEntity> clazz) {
		String key = db+":"+clazz.getCanonicalName()+":query";
		DBStoreCache queryCache = queryCaches.get(key);
		if(queryCache==null && getCacheFactory()!=null && cacheQueries) {
			synchronized(this) {
				if(queryCache==null) {
					queryCache = getCacheFactory().getCache(key);
					if(queryCache!=null) {
						queryCaches.put(key,queryCache);
					}
				}
			}
		}
		return queryCache;
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
