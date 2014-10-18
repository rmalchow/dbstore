package com.cinefms.dbstore.api;

public interface DBStoreListener {
	
	public boolean supports(Class<? extends DBStoreEntity> clazz);

	public void beforeSave(DBStoreEntity o);
	public void saved(DBStoreEntity o);
	
	public void beforeDelete(DBStoreEntity o);
	public void deleted(DBStoreEntity o);
	

}
