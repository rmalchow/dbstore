package com.cinefms.dbstore.api.impl;

import java.lang.reflect.ParameterizedType;

import com.cinefms.dbstore.api.DBStoreEntity;
import com.cinefms.dbstore.api.DBStoreListener;

public abstract class DBStoreListenerAdapter<T extends DBStoreEntity> implements DBStoreListener<T> {
	
	private Class<T> clazz;
	
	@SuppressWarnings("unchecked")
	public Class<T> getClazz() {
		if(clazz==null) {
			ParameterizedType superclass = (ParameterizedType)getClass().getGenericSuperclass();
			clazz = (Class<T>) superclass.getActualTypeArguments()[0];
		}
		return clazz;
	}

	public boolean supports(Class<? extends DBStoreEntity> clazz) {
		return clazz==getClazz();
	}

	public void beforeSave(String db, T o) {
	}

	public void updated(String db, T oldEntity, T newEntity) {
	}

	public void beforeDelete(String db, T o) {
	}

	public void deleted(String db, T o) {
	}

	public void created(String db, T o) {
	}
	

}
