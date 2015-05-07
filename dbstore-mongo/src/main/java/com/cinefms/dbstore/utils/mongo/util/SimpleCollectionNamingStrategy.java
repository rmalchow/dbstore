package com.cinefms.dbstore.utils.mongo.util;

public class SimpleCollectionNamingStrategy implements CollectionNamingStrategy {

	@Override
	public String getCollectionName(Class<?> clazz) {
		return clazz.getCanonicalName();
	}

}
