package com.cinefms.dbstore.utils.mongo;

import java.net.UnknownHostException;

import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class MongoServiceTest {
	
	@Test
	public void connect() throws UnknownHostException {
		MongoService ms = new MongoService();
		ms.setAuth(true);
		ms.setUsername("skjlls");
		ms.setPassword("skjlls");
		ms.setHosts("127.0.0.1:27017");
		ms.setDbName("skjlls");
		ms.setAuthDb("admin");
		DB db = ms.getClient().getDB("test");
		String s = "test_"+System.currentTimeMillis();
		DBCollection dbc = db.createCollection(s, new BasicDBObject());
		dbc.drop();
	}

}
