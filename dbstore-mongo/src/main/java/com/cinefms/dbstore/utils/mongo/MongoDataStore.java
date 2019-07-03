package com.cinefms.dbstore.utils.mongo;

import com.cinefms.dbstore.api.DataStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;
import org.springframework.beans.factory.annotation.Autowired;

public class MongoDataStore extends AMongoDataStore implements DataStore {

	private boolean checkUpdates = false;
	private ObjectMapper objectMapper;

	@Autowired
	private MongoService mongoService;

	private String defaultDb;
	private String dbPrefix;

	public ObjectMapper getObjectMapper() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}

	@Override
	public <T> boolean needsUpdate(T old, T object) {
		if (!checkUpdates) {
			return true;
		}
		if (old == null || object == null) {
			return true;
		}
		try {
			ObjectMapper om = getObjectMapper();
			byte[] o1 = om.writeValueAsBytes(old);
			byte[] o2 = om.writeValueAsBytes(object);

			if (o1.length != o2.length) {
				return true;
			}
			for (int i = 0; i < o1.length; i++) {
				if (o1[i] != o2[i]) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	public DB getDB(String db) {
		db = db == null ? defaultDb : (dbPrefix == null ? "" : (dbPrefix + "_")) + db;
		MongoService mongoService = getMongoService();
		log.debug("============================================================");
		log.debug("== ");
		log.debug("== ");
		log.debug("== getting DB from mongoService: " + mongoService);
		DB out = mongoService.getDb(db);
		log.debug("== ... result is: " + out);
		log.debug("== ");
		log.debug("============================================================");
		return out;
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


	public MongoService getMongoService() {
		return mongoService;
	}

	public void setMongoService(MongoService mongoService) {
		this.mongoService = mongoService;
	}

}
