package com.cinefms.dbstore.api.util;

import com.cinefms.dbstore.api.impl.BaseDBStoreEntity;


public class TestObject1 extends BaseDBStoreEntity {

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
