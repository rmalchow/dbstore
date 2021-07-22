package com.cinefms.dbstore.utils.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
//@ConditionalOnBean(MongoAutoConfiguration.class)
@PropertySource("datastore.properties")
public class SpringDataMongoDataStore extends AMongoDataStore {

	@Autowired
	private MongoClient mongo;

	@Value("${dbstore.defaultDb}")
	private String defaultDb;

	@Value("${dbstore.dbPrefix}")
	private String dbPrefix;

	@Override
	public MongoDatabase getDB(String db) {
		if (db == null) {
			return getMongo().getDatabase(defaultDb);
		} else {
			return getMongo().getDatabase((getDbPrefix() == null ? "" : (getDbPrefix() + "_")) + db);
		}
	}

	public MongoClient getMongo() {
		return mongo;
	}

	public void setMongo(MongoClient mongo) {
		this.mongo = mongo;
	}

	public String getDefaultDb() {
		return defaultDb;
	}

	public void setDefaultDb(String defaultDb) {
		this.defaultDb = defaultDb.trim();
	}

	public String getDbPrefix() {
		return dbPrefix;
	}

	public void setDbPrefix(String dbPrefix) {
		this.dbPrefix = dbPrefix.trim();
	}

}
