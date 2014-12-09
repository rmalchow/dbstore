package com.cinefms.dbstore.api;

public interface DBStoreListener {
	
	public boolean supports(Class<? extends DBStoreEntity> clazz);

	public void beforeSave(String db, DBStoreEntity o);
	public void updated(String db, DBStoreEntity oldEntity, DBStoreEntity newEntity);
	
	public void beforeDelete(String db, DBStoreEntity o);
	public void deleted(String db, DBStoreEntity o);
	

}
