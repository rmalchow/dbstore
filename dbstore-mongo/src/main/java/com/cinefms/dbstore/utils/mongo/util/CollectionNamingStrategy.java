package com.cinefms.dbstore.utils.mongo.util;

public interface CollectionNamingStrategy {

	String getCollectionName(Class<?> clazz);

}
