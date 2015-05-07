package com.cinefms.dbstore.utils.mongo;

import com.cinefms.dbstore.api.DataStore;

public class MongoDataStore extends AMongoDataStore implements DataStore {

	public String getCollectionName(Class clazz) {
		return clazz.getCanonicalName();
	}


}
