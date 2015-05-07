package com.cinefms.dbstore.utils.mongo.util;

public interface CollectionNamingStrategy {

	public String getCollectionName(Class<?> clazz);
	
	
}
