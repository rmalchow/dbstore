package com.cinefms.dbstore.api.util;

import com.cinefms.dbstore.api.DBStoreEntity;


public class TestObject1 implements DBStoreEntity {

	private static final long serialVersionUID = 1L;
	
	private String id;

	public TestObject1(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
