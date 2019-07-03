package com.cinefms.dbstore.api;

import com.cinefms.dbstore.api.exceptions.DBStoreException;
import com.cinefms.dbstore.query.api.DBStoreQuery;

import java.util.List;

public interface DataStore {

	<T extends DBStoreEntity> T saveObject(String db, T object) throws DBStoreException;

	<T extends DBStoreEntity> boolean deleteObject(String db, Class<T> clazz, String id) throws DBStoreException;

	<T extends DBStoreEntity> boolean deleteObject(String db, T object) throws DBStoreException;

	<T extends DBStoreEntity> void deleteObjects(String db, Class<T> type, DBStoreQuery query) throws DBStoreException;

	<T extends DBStoreEntity> T getObject(String db, Class<T> clazz, String id) throws DBStoreException;

	<T extends DBStoreEntity> List<T> findObjects(String db, Class<T> clazz, DBStoreQuery query) throws DBStoreException;

	<T extends DBStoreEntity> int countObjects(String db, Class<T> clazz, DBStoreQuery query) throws DBStoreException;

	<T extends DBStoreEntity> T findObject(String db, Class<T> clazz, DBStoreQuery query) throws DBStoreException;

	void deleteBinary(String db, String bucket, String id) throws DBStoreException;

	DBStoreBinary getBinary(String db, String bucket, String filename) throws DBStoreException;

	void storeBinary(String db, String bucket, DBStoreBinary binary) throws DBStoreException;

	void addListener(DBStoreListener listener);

	<T> void saveObjects(String db, List<T> objects) throws DBStoreException;

}
