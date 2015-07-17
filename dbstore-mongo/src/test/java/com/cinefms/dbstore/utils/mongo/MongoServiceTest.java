package com.cinefms.dbstore.utils.mongo;

import java.net.UnknownHostException;

import org.junit.Test;

public class MongoServiceTest {
	
	@Test
	public void connect() throws UnknownHostException {
		MongoService ms = new MongoService();
		ms.setAuth(true);
		ms.setUsername("aaa");
		ms.setPassword("bbb");
		ms.setHosts("localhost");
		ms.setDbName("skjlls");
		ms.getClient();
	}

}
