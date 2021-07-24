package com.cinefms.dbstore.utils.mongo.entities;

import com.cinefms.dbstore.api.impl.BaseDBStoreEntity;

import java.beans.ConstructorProperties;

public class SimpleEntity extends BaseDBStoreEntity {

	public String value;

	@ConstructorProperties("data")
	public SimpleEntity(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
