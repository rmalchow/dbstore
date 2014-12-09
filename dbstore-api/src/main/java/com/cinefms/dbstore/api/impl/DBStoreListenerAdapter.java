package com.cinefms.dbstore.api.impl;

import com.cinefms.dbstore.api.DBStoreEntity;
import com.cinefms.dbstore.api.DBStoreListener;

public abstract class DBStoreListenerAdapter<T extends DBStoreEntity> implements DBStoreListener {
	
	private Class<T> clazz;
	
	protected DBStoreListenerAdapter(Class<T> clazz) {
		this.clazz = clazz;
	}

	public boolean supports(Class<? extends DBStoreEntity> clazz) {
		return clazz==this.clazz;
	}

	public void beforeSave(String db, DBStoreEntity o) {
	}

	public void updated(String db, DBStoreEntity oldEntity, DBStoreEntity newEntity) {
	}

	public void beforeDelete(String db, DBStoreEntity o) {
	}

	public void deleted(String db, DBStoreEntity o) {
	}

	public void created(String db, DBStoreEntity o) {
	}
	

}
