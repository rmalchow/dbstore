package com.cinefms.dbstore.utils.mongo;

import com.cinefms.dbstore.api.DataStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

public class MongoDataStore extends AMongoDataStore implements DataStore {

	@Autowired
	private MongoService mongoService;

	private boolean checkUpdates = false;
	private String defaultDb;
	private String dbPrefix;

	private ObjectMapper objectMapper;

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

			return !Arrays.equals(o1, o2);

		} catch (Exception e) {
			return true;
		}
	}

	@Override
	public MongoDatabase getDB(String db) {
		if (db == null) {
			db = defaultDb;
		} else {
			db = (dbPrefix == null ? "" : (dbPrefix + "_")) + db;
		}
		MongoService ms = getMongoService();
		log.debug("============================================================");
		log.debug("== ");
		log.debug("== ");
		log.debug("== getting DB from mongoService: " + ms);
		MongoDatabase out = ms.getDb(db);
		log.debug("== ... result is: " + out);
		log.debug("== ");
		log.debug("============================================================");
		return out;
	}

	public boolean isCheckUpdates() {
		return checkUpdates;
	}

	public MongoDataStore setCheckUpdates(boolean checkUpdates) {
		this.checkUpdates = checkUpdates;
		return this;
	}

	public String getDefaultDb() {
		return defaultDb;
	}

	public MongoDataStore setDefaultDb(String defaultDb) {
		this.defaultDb = defaultDb;
		return this;
	}

	public String getDbPrefix() {
		return dbPrefix;
	}

	public MongoDataStore setDbPrefix(String dbPrefix) {
		this.dbPrefix = dbPrefix;
		return this;
	}

	public MongoService getMongoService() {
		return mongoService;
	}

	public MongoDataStore setMongoService(MongoService mongoService) {
		this.mongoService = mongoService;
		return this;
	}

}
