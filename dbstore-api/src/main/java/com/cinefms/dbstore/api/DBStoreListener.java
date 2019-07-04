package com.cinefms.dbstore.api;

public interface DBStoreListener<T extends DBStoreEntity> {

	boolean supports(Class<? extends DBStoreEntity> clazz);

	void beforeSave(String db, T o);

	void beforeDelete(String db, T o);

	void created(String db, T o);

	void updated(String db, T oldEntity, T newEntity);

	void deleted(String db, T o);

}
