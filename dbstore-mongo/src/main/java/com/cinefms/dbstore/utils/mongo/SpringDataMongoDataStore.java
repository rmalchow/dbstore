package com.cinefms.dbstore.utils.mongo;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

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

	@Override
	public DB getDB(String db) throws UnknownHostException {
		db = db==null?defaultDb:(dbPrefix==null?"":(dbPrefix+"_"))+db;
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

	
	
}
