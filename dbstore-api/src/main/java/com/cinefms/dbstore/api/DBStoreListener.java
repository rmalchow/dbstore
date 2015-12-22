package com.cinefms.dbstore.api;

public interface DBStoreListener<T extends DBStoreEntity> {
	
	public boolean supports(Class<? extends DBStoreEntity> clazz);

	public void beforeSave(String db, T o);
	public void beforeDelete(String db, T o);

	public void created(String db, T o);
	public void updated(String db, T oldEntity, T newEntity);
	public void deleted(String db, T o);
	

}
