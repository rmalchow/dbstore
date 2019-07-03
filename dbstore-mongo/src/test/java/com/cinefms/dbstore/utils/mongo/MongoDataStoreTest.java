package com.cinefms.dbstore.utils.mongo;

import com.cinefms.dbstore.query.api.impl.BasicQuery;
import com.cinefms.dbstore.utils.mongo.util.TestObject1;
import com.cinefms.dbstore.utils.mongo.util.TestObject1A;
import com.cinefms.dbstore.utils.mongo.util.TestObject1B;
import com.mongodb.MongoClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class MongoDataStoreTest {


	private String dataStoreName;
	private MongoDataStore mds;


	@Before
	public void createDataStore() throws Exception {
		dataStoreName = ("__junit_test_" + Math.random()).replaceAll("\\.", "_");
		MongoService ms = new MongoService();
		ms.setDbName(dataStoreName);
		ms.setHosts("127.0.0.1:27017");
		mds = new MongoDataStore();
		mds.setMongoService(ms);
		mds.setDbPrefix(dataStoreName + "_prefix");
		mds.setDefaultDb(dataStoreName);
	}

	@After
	public void deleteDataStore() {
		if (dataStoreName != null) {
			try {
				MongoClient client = new MongoClient("127.0.0.1", 27017);
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

	@Test
	public void testSimpleDBCreationExpectSuccess() throws Exception {
		TestObject1 to1 = new TestObject1();
		to1 = mds.saveObject("hello", to1);
		Assert.assertNotNull(to1.getId());
	}

	@Test
	public void testSimplePolymorphicExpectSuccess() throws Exception {
		TestObject1A to1a = new TestObject1A();
		to1a = mds.saveObject("hello", to1a);
		TestObject1B to1b = new TestObject1B();
		to1b = mds.saveObject("hello", to1b);
		Assert.assertNotNull(to1a.getId());
		Assert.assertNotNull(to1b.getId());
	}

	@Test
	public void testSimpleCRUDCreationExpectSuccess() throws Exception {
		TestObject1 to1 = new TestObject1();
		to1 = mds.saveObject("hello", to1);
		to1 = mds.findObject("hello", TestObject1.class, BasicQuery.createQuery().eq("_id", to1.getId()));
		Assert.assertNotNull(to1);
		List<TestObject1> os = mds.findObjects("hello", TestObject1.class, BasicQuery.createQuery().eq("_id", to1.getId()));
		Assert.assertNotNull(os);
		Assert.assertEquals(1, os.size());

		// delete
		boolean b1 = mds.deleteObject("hello", to1);
		Assert.assertEquals(true, b1);

		// delete again
		boolean b2 = mds.deleteObject("hello", TestObject1.class, to1.getId());
		Assert.assertEquals(false, b2);

		// save and delete again
		to1 = mds.saveObject("hello", to1);
		boolean b3 = mds.deleteObject("hello", TestObject1.class, to1.getId());
		Assert.assertEquals(true, b3);
	}


}
