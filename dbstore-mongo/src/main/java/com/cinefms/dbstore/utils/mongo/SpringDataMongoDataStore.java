package com.cinefms.dbstore.utils.mongo;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.DB;
import com.mongodb.Mongo;

@Configuration
@ConditionalOnClass(MongoAutoConfiguration.class)
@PropertySource("datastore.properties")
public class SpringDataMongoDataStore extends AMongoDataStore {
	
	@Autowired
	private Mongo mongo;
	
	@Value("dbstore.defaultDb")
	private String defaultDb; 

	@Value("dbstore.dbPrefix")
	private String dbPrefix; 
	
	@Override
	public DB getDB(String db) throws UnknownHostException {
		db = db==null?defaultDb:(getDbPrefix()==null?"":(getDbPrefix()+"_"))+db;
		return getMongo().getDB(db);
	}

	public Mongo getMongo() {
		return mongo;
	}

	public void setMongo(Mongo mongo) {
		this.mongo = mongo;
	}
	
	public String getDefaultDb() {
		return defaultDb;
	}

	public void setDefaultDb(String defaultDb) {
		this.defaultDb = defaultDb;
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
		this.dbPrefix = dbPrefix;
	}
	
	
}
