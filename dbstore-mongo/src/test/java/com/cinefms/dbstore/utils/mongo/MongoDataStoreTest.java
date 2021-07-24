package com.cinefms.dbstore.utils.mongo;

import com.mongodb.MongoClient;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;

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
				MongoClient client = new MongoClient(mariaDBContainer.getHost(), mariaDBContainer.getPort());
				for (String s : client.getDatabaseNames()) {
					if (s.startsWith(dataStoreName)) {
						client.dropDatabase(s);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
