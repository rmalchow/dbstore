package com.cinefms.dbstore.utils.mongo;


public class Main {

	
	public static void main(String[] args) {
		try {
			
			
			MongoService ms = new MongoService();
			ms.setHosts("192.168.1.11:27017,192.168.1.12:27017,192.168.1.13:27017");
			ms.getClient().getDatabase("skjlls_default").createCollection("aaa");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
}
