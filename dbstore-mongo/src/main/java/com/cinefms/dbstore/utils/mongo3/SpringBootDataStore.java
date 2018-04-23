package com.cinefms.dbstore.utils.mongo3;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cinefms.dbstore.api.DBStoreBinary;
import com.cinefms.dbstore.api.DBStoreEntity;
import com.cinefms.dbstore.api.DBStoreListener;
import com.cinefms.dbstore.api.DataStore;
import com.cinefms.dbstore.api.exceptions.DBStoreException;
import com.cinefms.dbstore.api.exceptions.EntityNotFoundException;
import com.cinefms.dbstore.query.api.DBStoreQuery;
import com.mongodb.MongoClient;

@Component
public class SpringBootDataStore implements DataStore {
	
	@Autowired
	private MongoClient mongoClient;
	

	@Override
	public <T extends DBStoreEntity> T saveObject(String db, T object) throws DBStoreException, EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DBStoreEntity> boolean deleteObject(String db, Class<T> clazz, String id)
			throws DBStoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends DBStoreEntity> boolean deleteObject(String db, T object) throws DBStoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends DBStoreEntity> void deleteObjects(String db, Class<T> type, DBStoreQuery query)
			throws DBStoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends DBStoreEntity> T getObject(String db, Class<T> clazz, String id) throws DBStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DBStoreEntity> List<T> findObjects(String db, Class<T> clazz, DBStoreQuery query)
			throws DBStoreException, EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DBStoreEntity> int countObjects(String db, Class<T> clazz, DBStoreQuery query)
			throws DBStoreException, EntityNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T extends DBStoreEntity> T findObject(String db, Class<T> clazz, DBStoreQuery query)
			throws DBStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteBinary(String db, String bucket, String id) throws DBStoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public DBStoreBinary getBinary(String db, String bucket, String filename) throws DBStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeBinary(String db, String bucket, DBStoreBinary binary) throws DBStoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addListener(DBStoreListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> void saveObjects(String db, List<T> objects) throws DBStoreException {
		// TODO Auto-generated method stub

	}

}
