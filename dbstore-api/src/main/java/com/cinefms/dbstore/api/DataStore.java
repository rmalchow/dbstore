package com.cinefms.dbstore.api;

import java.util.List;

import com.cinefms.dbstore.api.exceptions.DBStoreException;
import com.cinefms.dbstore.api.exceptions.EntityNotFoundException;

public interface DataStore {

	public <T extends DBStoreEntity> T saveObject(String db, T object) throws DBStoreException, EntityNotFoundException;
	
	public <T extends DBStoreEntity> boolean deleteObject(String db, Class<T> clazz, String id) throws DBStoreException;

	public <T extends DBStoreEntity> boolean deleteObject(String db, T object) throws DBStoreException;

	public <T extends DBStoreEntity> T getObject(String db, Class<T> clazz, String id) throws DBStoreException;

	public <T extends DBStoreEntity> List<T> findObjects(String db, Class<T> clazz, DBStoreQuery query) throws DBStoreException, EntityNotFoundException;

	public <T extends DBStoreEntity> int countObjects(String db, Class<T> clazz, DBStoreQuery query) throws DBStoreException, EntityNotFoundException;
	
	public <T extends DBStoreEntity> T findObject(String db, Class<T> clazz, DBStoreQuery query) throws DBStoreException;

	public abstract void deleteBinary(String db, String bucket, String id) throws DBStoreException;

	public abstract DBStoreBinary getBinary(String db, String bucket, String filename) throws DBStoreException;

	public abstract void storeBinary(String db, String bucket, DBStoreBinary binary) throws DBStoreException;
	
	public void addListener(DBStoreListener listener);
	
	
}
