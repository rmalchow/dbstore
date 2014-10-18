package com.cinefms.dbstore.api;

import java.util.List;

import com.cinefms.dbstore.api.exceptions.DatabaseException;
import com.cinefms.dbstore.api.exceptions.EntityNotFoundException;

public interface DataStore {

	public <T extends Entity> T saveObject(T object) throws DatabaseException, EntityNotFoundException;
	
	public <T extends Entity> boolean deleteObject(Class<T> clazz, String id) throws DatabaseException;

	public <T extends Entity> boolean deleteObject(T object) throws DatabaseException;

	public <T extends Entity> T getObject(Class<T> clazz, String id) throws DatabaseException;

	public <T extends Entity> List<T> findObjects(Class<T> clazz, Query query) throws DatabaseException, EntityNotFoundException;

	public <T extends Entity> T findObject(Class<T> clazz, Query query) throws DatabaseException;

	public abstract void deleteBinary(String bucket, String id) throws DatabaseException;

	public abstract Binary getBinary(String bucket, String filename) throws DatabaseException;

	public abstract void storeBinary(String bucket, Binary binary) throws DatabaseException;
	
}
