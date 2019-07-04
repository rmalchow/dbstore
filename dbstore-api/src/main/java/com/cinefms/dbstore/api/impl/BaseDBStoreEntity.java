package com.cinefms.dbstore.api.impl;

import com.cinefms.dbstore.api.DBStoreEntity;


public class BaseDBStoreEntity implements DBStoreEntity {

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String createId() {
		return null;
	}


}
