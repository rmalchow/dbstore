package com.cinefms.dbstore.utils.mongo;

import com.cinefms.dbstore.api.DataStore;
import com.cinefms.dbstore.utils.mongo.util.CollectionNamingStrategy;

public class MongoDataStore extends AMongoDataStore implements DataStore {

	private CollectionNamingStrategy collectionNamingStrategy;
	
	public String getCollectionName(Class<?> clazz) {
		return collectionNamingStrategy.getCollectionName(clazz);
	}

	public CollectionNamingStrategy getCollectionNamingStrategy() {
		return collectionNamingStrategy;
	}

	public void setCollectionNamingStrategy(CollectionNamingStrategy collectionNamingStrategy) {
		this.collectionNamingStrategy = collectionNamingStrategy;
	}


}
