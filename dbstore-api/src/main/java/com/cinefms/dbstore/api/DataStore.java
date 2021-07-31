package com.cinefms.dbstore.api;

import java.util.List;

import com.cinefms.dbstore.api.exceptions.DBStoreException;
import com.cinefms.dbstore.query.api.DBStoreQuery;

public interface DataStore {

	<T extends DBStoreEntity> T saveObject(String db, T object);

	<T extends DBStoreEntity> boolean deleteObject(String db, Class<T> clazz, String id);

	<T extends DBStoreEntity> boolean deleteObject(String db, T object);

	<T extends DBStoreEntity> boolean deleteObjects(String db, Class<T> type, DBStoreQuery query);

	<T extends DBStoreEntity> T getObject(String db, Class<T> clazz, String id);

	<T extends DBStoreEntity> List<T> findObjects(String db, Class<T> clazz, DBStoreQuery query);

	<T extends DBStoreEntity> long countObjects(String db, Class<T> clazz, DBStoreQuery query);

	<T extends DBStoreEntity> T findObject(String db, Class<T> clazz, DBStoreQuery query);

	void addListener(DBStoreListener<?> listener);
	
	public void saveBinary(String dbName, String bucket, DBStoreBinary binary) throws DBStoreException;
	
	public DBStoreBinary getBinary(String dbName, String bucket, String id) throws DBStoreException;

	<T extends DBStoreEntity> List<T> saveObjects(String db, List<T> objects);

}
