package com.cinefms.dbstore.utils.mongo;

import com.cinefms.dbstore.api.DBStoreEntity;
import com.cinefms.dbstore.api.DBStoreListener;
import com.cinefms.dbstore.api.DataStore;
import com.cinefms.dbstore.api.annotations.Index;
import com.cinefms.dbstore.api.annotations.Indexes;
import com.cinefms.dbstore.api.annotations.Write;
import com.cinefms.dbstore.api.annotations.WriteMode;
import com.cinefms.dbstore.api.exceptions.DBStoreException;
import com.cinefms.dbstore.query.api.DBStoreQuery;
import com.cinefms.dbstore.query.api.impl.BasicQuery;
import com.cinefms.dbstore.query.mongo.QueryMongojackTranslator;
import com.cinefms.dbstore.utils.mongo.util.CollectionNamingStrategy;
import com.cinefms.dbstore.utils.mongo.util.SimpleCollectionNamingStrategy;
import com.mongodb.WriteConcern;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.UuidRepresentation;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.mongojack.JacksonMongoCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public abstract class AMongoDataStore implements DataStore {

	protected static final Log log = LogFactory.getLog(AMongoDataStore.class);
	private final Map<String, JacksonMongoCollection<?>> collections = new HashMap<>();
	private final Map<String, List<DBStoreListener<?>>> listenerMap = new HashMap<>();
	private final QueryMongojackTranslator fqtl = new QueryMongojackTranslator();

	@Autowired(required = false)
	private List<DBStoreListener<?>> listeners = new ArrayList<>();
	private CollectionNamingStrategy collectionNamingStrategy = new SimpleCollectionNamingStrategy();

	public abstract MongoDatabase getDB(String db);

	private <T> MongoCollection<T> initializeCollection(MongoDatabase db, Class<T> clazz) {
		String collectionName = getCollectionName(clazz);
		MongoCollection<T> dbc = db.getCollection(collectionName, clazz);

		if (clazz.getAnnotation(Indexes.class) != null) {
			for (Index i : clazz.getAnnotation(Indexes.class).value()) {

				Bson idx = com.mongodb.client.model.Indexes.ascending(i.fields());

				IndexOptions options = new IndexOptions();
				options.unique(i.unique());

				log.debug(" === CREATING INDEX: " + idx + " ==== ");
				dbc.createIndex(idx, options);
			}
		}

		return dbc;
	}

	@SuppressWarnings("unchecked")
	private <T> JacksonMongoCollection<T> getCollection(String db, Class<T> clazz) {
		String collectionName = getCollectionName(clazz);
		String key = db + ":" + collectionName;
		log.debug(" == DB        : " + db);
		log.debug(" == Collection: " + collectionName);

		try {
			JacksonMongoCollection<T> out = (JacksonMongoCollection<T>) collections.get(key);

			if (out == null) {
				log.debug("============================================================");
				log.debug("==");
				log.debug("== DB COLLECTION NOT CREATED .... (creating...) ");
				log.debug("==");
				log.debug("==  CLAZZ IS: " + clazz.getCanonicalName());
				log.debug("== DBNAME IS: " + db);
				MongoDatabase d = getDB(db);
				log.debug("==     DB IS: " + d);
				MongoCollection<T> dbc = initializeCollection(d, clazz);
				log.debug("==    DBC IS: " + dbc);
				out = JacksonMongoCollection.builder().build(dbc, clazz, UuidRepresentation.JAVA_LEGACY);
				if (clazz.getAnnotation(Write.class) != null && clazz.getAnnotation(Write.class).value() == WriteMode.FAST) {
					out.withWriteConcern(WriteConcern.UNACKNOWLEDGED);
				}
				collections.put(key, out);
				log.debug("==");
				log.debug("============================================================");
			}

			return out;

		} catch (Exception e) {
			throw new RuntimeException("Unable to obtain collection '" + collectionName + "'", e);
		}
	}

	private List<DBStoreListener<?>> getListeners(Class<? extends DBStoreEntity> clazz) {
		List<DBStoreListener<?>> out = listenerMap.get(clazz.getCanonicalName());

		if (out == null) {
			out = new ArrayList<>();

			if (listeners != null) {
				for (DBStoreListener<?> l : listeners) {
					if (l.supports(clazz)) {
						log.debug("listeners on " + clazz + ": " + l.getClass() + " supports");
						out.add(l);

					} else {
						log.debug("listeners on " + clazz + ": " + l.getClass() + " does not support");
					}
				}
			}

			listenerMap.put(clazz.getCanonicalName(), out);
		}

		log.debug("listeners on " + clazz + ": " + out.size());
		return out;
	}

	@Override
	public <T extends DBStoreEntity> T findObject(String db, Class<T> clazz, DBStoreQuery query) {
		return getCollection(db, clazz)
				.find(fqtl.translate(query))
				.sort(fqtl.translateOrderBy(query))
				.limit(1)
				.first();
	}

	@Override
	public <T extends DBStoreEntity> long countObjects(String db, Class<T> clazz, DBStoreQuery query) {
		return getCollection(db, clazz).countDocuments(fqtl.translate(query));
	}

	@Override
	public <T extends DBStoreEntity> List<T> findObjects(String db, Class<T> clazz, DBStoreQuery query) {
		FindIterable<T> f = getCollection(db, clazz)
				.find(fqtl.translate(query))
				.sort(fqtl.translateOrderBy(query));

		int skip = query.getStart();
		if (skip > 0) {
			f = f.skip(skip);
		}

		int max = query.getMax();
		if (max > 0 && max < Integer.MAX_VALUE) {
			f = f.limit(max);
		}

		log.debug(" ---> LIMIT (" + skip + ":" + max + ")");

		List<T> out = new LinkedList<>();
		f.forEach(out::add);

		log.debug("-- db query: found " + out.size() + " matches for query (" + clazz.getCanonicalName() + ":" + query + ")");

		return out;
	}

	@Override
	public <T extends DBStoreEntity> T getObject(String db, Class<T> clazz, String id) {
		return getCollection(db, clazz).findOneById(id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends DBStoreEntity> boolean deleteObject(String db, T object) {
		return object != null && deleteObject(db, object.getClass(), object.getId());
	}

	@Override
	public <T extends DBStoreEntity> boolean deleteObject(String db, Class<T> clazz, String id) {
		return id != null && deleteObjects(db, clazz, BasicQuery.createQuery().eq("_id", id));
	}

	@Override
	public <T extends DBStoreEntity> boolean deleteObjects(String db, Class<T> clazz, DBStoreQuery query) {
		List<DBStoreListener<?>> entityListeners = getListeners(clazz);

		boolean anyDeleted = false;

		for (T object : getCollection(db, clazz).find(fqtl.translate(query))) {
			if (object == null || object.getId() == null) {
				continue;
			}

			for (DBStoreListener listener : entityListeners) {
				log.debug("firing 'beforeDelete' for: " + clazz + " / " + object.getId());
				listener.beforeDelete(db, object);
			}

			try {
				getCollection(db, clazz).removeById(object.getId());

				for (DBStoreListener listener : entityListeners) {
					log.debug("firing 'delete' for: " + clazz + " / " + object.getId());
					listener.deleted(db, object);
				}


				anyDeleted = true;

			} catch (Exception e) {
				throw new DBStoreException(e);
			}
		}

		return anyDeleted;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends DBStoreEntity> List<T> saveObjects(String db, List<T> objects) {
		if (objects.isEmpty()) return objects;

		Class<T> clazz = (Class<T>) objects.stream().findFirst().map(Object::getClass).orElse(null);

		List<DBStoreListener<?>> entityListeners = getListeners(clazz);
		List<T> out = new ArrayList<>(objects.size());

		for (T object : objects) {
			log.debug(clazz + " / saving object: " + object.getId() + ", notifying " + entityListeners.size() + " listeners");

			for (DBStoreListener listener : entityListeners) {
				log.debug("firing 'beforeSave' for: " + clazz + " / " + object.getId());
				listener.beforeSave(db, object);
			}

			JacksonMongoCollection<T> coll = (JacksonMongoCollection<T>) getCollection(db, object.getClass());

			T old = null;
			if (object.getId() != null) {
				old = coll.findOneById(object.getId());
			} else {
				String id = object.createId();
				if (id == null) {
					id = ObjectId.get().toString();
				}
				object.setId(id);
			}

			if (old != null) {
				if (!needsUpdate(old, object)) {
					log.debug("no change, returning");
					out.add(object);
					continue;
				}

				coll.replaceOne(Filters.eq("_id", old.getId()), object);
			} else {
				coll.save(object);
			}

			object = getObject(db, clazz, object.getId());

			for (DBStoreListener listener : entityListeners) {
				if (old != null) {
					log.debug("firing 'updated' for: " + out.getClass() + " / " + object.getId() + " / " + listener.getClass());
					listener.updated(db, old, object);

				} else {
					log.debug("firing 'created' for: " + out.getClass() + " / " + object.getId() + " / " + listener.getClass());
					listener.created(db, object);
				}
			}

			out.add(object);
		}

		return out;
	}

	public <T extends DBStoreEntity> T saveObject(String db, T object) {
		return saveObjects(db, Collections.singletonList(object))
				.stream()
				.findFirst()
				.orElse(null);
	}

	public <T> boolean needsUpdate(T old, T object) {
		return true;
	}

	@Override
	public void addListener(DBStoreListener<?> listener) {
		this.listeners.add(listener);
	}

	public String getCollectionName(Class<?> clazz) {
		return collectionNamingStrategy.getCollectionName(clazz);
	}

	public CollectionNamingStrategy getCollectionNamingStrategy() {
		return collectionNamingStrategy;
	}

	public void setCollectionNamingStrategy(CollectionNamingStrategy collectionNamingStrategy) {
		this.collectionNamingStrategy = collectionNamingStrategy;
	}

}
