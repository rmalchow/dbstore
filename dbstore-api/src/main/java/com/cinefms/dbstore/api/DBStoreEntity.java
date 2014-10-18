package com.cinefms.dbstore.api;

import javax.persistence.Id;


public abstract class DBStoreEntity {

	@Id
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	

}
