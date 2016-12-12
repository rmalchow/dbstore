package com.cinefms.dbstore.utils.mongo;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;

import com.cinefms.dbstore.api.DataStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;

public class MongoDataStore extends AMongoDataStore implements DataStore {

	private boolean checkUpdates = false;
	private ObjectMapper objectMapper;
	@Autowired
	private MongoService mongoService;
	
	public ObjectMapper getObjectMapper() {
		if(objectMapper==null) {
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}
	
	@Override
	public <T> boolean needsUpdate(T old, T object) {
		if(!checkUpdates) {
			return true;
		}
		if(old==null || object==null) {
			return true;
		}
		try {
			ObjectMapper om = getObjectMapper();
			byte[] o1 = om.writeValueAsBytes(old);
			byte[] o2 = om.writeValueAsBytes(object);
			
			if(o1.length!=o2.length) {
				return true;
			}
			for(int i=0;i<o1.length;i++) {
				if(o1[i]!=o2[i]) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	public DB getDB(String db) throws UnknownHostException {
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

	public MongoService getMongoService() {
		return mongoService;
	}

	public void setMongoService(MongoService mongoService) {
		this.mongoService = mongoService;
	}

}
