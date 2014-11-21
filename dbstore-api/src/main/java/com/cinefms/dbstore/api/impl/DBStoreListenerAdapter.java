package com.cinefms.dbstore.api.impl;

import com.cinefms.dbstore.api.DBStoreEntity;
import com.cinefms.dbstore.api.DBStoreListener;

public abstract class DBStoreListenerAdapter<T> implements DBStoreListener {
	
	private Class<T> clazz;
	
	protected DBStoreListenerAdapter(Class<T> clazz) {
		this.clazz = clazz;
	}

	protected void doBeforeSave(String db, T o) {
	}

	protected void doAfterSaved(String db, T o) {
	}

	protected void doUpdate(String db, T oldEntity, T newEntity) {
	}
	
	protected void doBeforeDelete(String db, T o) {
	}

	protected void doAfterDeleted(String db, T o) {
	}

	public boolean supports(Class<? extends DBStoreEntity> clazz) {
		return this.clazz == clazz;
	}

	public void beforeSave(String db, DBStoreEntity o) {
		doBeforeSave(db, (T)o);
	}

	public void update(String db, DBStoreEntity oldEntity, DBStoreEntity newEntity) {
		doUpdate(db, (T)oldEntity, (T)newEntity);
	}
	
	public void saved(String db, DBStoreEntity o) {
		doAfterSaved(db, (T)o);
	}

	public void beforeDelete(String db, DBStoreEntity o) {
		doBeforeDelete(db, (T)o);
	}

	public void deleted(String db, DBStoreEntity o) {
		doAfterDeleted(db, (T)o);
	}

}
