package com.cinefms.dbstore.utils.mongo;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.DB;
import com.mongodb.MongoClient;

@Component
public class SpringDataMongoDataStore extends AMongoDataStore {
	
	protected static Log log = LogFactory.getLog(SpringDataMongoDataStore.class);
	
	@Autowired
	private MongoClient mongo;
	
	@Value("${dbstore.defaultDb}")
	private String defaultDb; 

	@Value("${dbstore.dbPrefix}")
	private String dbPrefix; 
	
	@Override
	public DB getDB(String db) throws UnknownHostException {
		db = db==null?defaultDb:(getDbPrefix()==null?"":(getDbPrefix()+"_"))+db;
		log.info(" = DB NAME: "+db);
		return mongo.getDB(db);
	}

	public String getDefaultDb() {
		return defaultDb;
	}

	public void setDefaultDb(String defaultDb) {
		this.defaultDb = defaultDb.trim();
	}

	@Override
	public String toString() {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("class", this.getClass().getCanonicalName());
		map.put("dbPrefix", getDbPrefix());
		map.put("defaultDb", getDefaultDb());
		map.put("serverAddresses", mongo.getAllAddress());
		try {
			return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(map);
		} catch (JsonProcessingException e) {
			return "[error]";
		}
	}

	public String getDbPrefix() {
		return dbPrefix;
	}

	public void setDbPrefix(String dbPrefix) {
		this.dbPrefix = dbPrefix.trim();
	}
	
	
}
