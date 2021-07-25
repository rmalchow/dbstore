package com.cinefms.dbstore.utils.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;

import java.util.LinkedList;
import java.util.List;

public abstract class MongoDataStoreTest {

	@ClassRule
	public static final TestMongoDBContainer mariaDBContainer = TestMongoDBContainer.getInstance();

	public String dataStoreName;
	public MongoDataStore mds;

	@Before
	public void setup() {
		dataStoreName = ("__junit_test_" + Math.random()).replaceAll("\\.", "_");
		MongoService ms = new MongoService();
		ms.setDbName(dataStoreName);
		ms.setHosts(mariaDBContainer.getHost() + ":" + mariaDBContainer.getPort());
		mds = new MongoDataStore();
		mds.setMongoService(ms);
		mds.setDbPrefix(dataStoreName + "_prefix");
		mds.setDefaultDb(dataStoreName);
	}

	@After
	public void cleanup() {
		if (dataStoreName != null) {
			try {
				MongoClient client = MongoClients.create("mongodb://" + mariaDBContainer.getHost() + ":" + mariaDBContainer.getPort());
				for (String s : client.listDatabaseNames()) {
					if (s.startsWith(dataStoreName)) {
						client.getDatabase(s).drop();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	List<Document> loadAll(Iterable<Document> iterable) {
		List<Document> out = new LinkedList<>();
		iterable.forEach(out::add);
		return out;
	}

}
