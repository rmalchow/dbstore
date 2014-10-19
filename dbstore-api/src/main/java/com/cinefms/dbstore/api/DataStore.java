package com.cinefms.dbstore.api;

import java.util.List;

import com.cinefms.dbstore.api.exceptions.DBStoreException;
import com.cinefms.dbstore.api.exceptions.EntityNotFoundException;

public interface DataStore {

	public <T extends DBStoreEntity> T saveObject(T object) throws DBStoreException, EntityNotFoundException;
	
	public <T extends DBStoreEntity> boolean deleteObject(Class<T> clazz, String id) throws DBStoreException;

	public <T extends DBStoreEntity> boolean deleteObject(T object) throws DBStoreException;

	public <T extends DBStoreEntity> T getObject(Class<T> clazz, String id) throws DBStoreException;

	public <T extends DBStoreEntity> List<T> findObjects(Class<T> clazz, DBStoreQuery query) throws DBStoreException, EntityNotFoundException;

	public <T extends DBStoreEntity> T findObject(Class<T> clazz, DBStoreQuery query) throws DBStoreException;

	public abstract void deleteBinary(String bucket, String id) throws DBStoreException;

	public abstract DBStoreBinary getBinary(String bucket, String filename) throws DBStoreException;

	public abstract void storeBinary(String bucket, DBStoreBinary binary) throws DBStoreException;
	
	public void addListener(DBStoreListener listener);
	
	
}
