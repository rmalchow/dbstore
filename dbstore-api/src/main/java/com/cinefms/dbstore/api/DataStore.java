package com.cinefms.dbstore.api;

import java.util.List;

import com.cinefms.dbstore.api.exceptions.DatabaseException;
import com.cinefms.dbstore.api.exceptions.EntityNotFoundException;

public interface DataStore {

	public <T extends DBStoreEntity> T saveObject(T object) throws DatabaseException, EntityNotFoundException;
	
	public <T extends DBStoreEntity> boolean deleteObject(Class<T> clazz, String id) throws DatabaseException;

	public <T extends DBStoreEntity> boolean deleteObject(T object) throws DatabaseException;

	public <T extends DBStoreEntity> T getObject(Class<T> clazz, String id) throws DatabaseException;

	public <T extends DBStoreEntity> List<T> findObjects(Class<T> clazz, DBStoreQuery query) throws DatabaseException, EntityNotFoundException;

	public <T extends DBStoreEntity> T findObject(Class<T> clazz, DBStoreQuery query) throws DatabaseException;

	public abstract void deleteBinary(String bucket, String id) throws DatabaseException;

	public abstract DBStoreBinary getBinary(String bucket, String filename) throws DatabaseException;

	public abstract void storeBinary(String bucket, DBStoreBinary binary) throws DatabaseException;
	
	public void addListener(DBStoreListener listener);
	
	
}
