package com.cinefms.dbstore.utils.mongo.util;

import com.cinefms.dbstore.api.DBStoreEntity;

public class TestObject1 implements DBStoreEntity {

	private String id;

	public TestObject1() {
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	

}
