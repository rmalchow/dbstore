package com.cinefms.dbstore.utils.mongo;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.DB;
import com.mongodb.Mongo;

@Component
//@ConditionalOnBean(MongoAutoConfiguration.class)
@PropertySource("datastore.properties")
public class SpringDataMongoDataStore extends AMongoDataStore {

	protected static Log log = LogFactory.getLog(SpringDataMongoDataStore.class);

	@Autowired
	private Mongo mongo;

	@Value("${dbstore.defaultDb}")
	private String defaultDb;

	@Value("${dbstore.dbPrefix}")
	private String dbPrefix;

	@Override
	public DB getDB(String db) {
		return getMongo().getDB(db == null ? defaultDb : (getDbPrefix() == null ? "" : (getDbPrefix() + "_")) + db);
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
		this.defaultDb = defaultDb.trim();
	}

	@Override
	public String toString() {
		Map<String, Object> map = new HashMap<>();
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
