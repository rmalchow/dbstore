package com.cinefms.dbstore.utils.mongo.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cinefms.dbstore.cache.api.annotations.CollectionName;

public class SimpleCollectionNamingStrategy implements CollectionNamingStrategy {

	private static Log log = LogFactory.getLog(SimpleCollectionNamingStrategy.class);
	
	@Override
	public String getCollectionName(Class<?> clazz) {
		if(clazz.getAnnotation(CollectionName.class)!=null) {
			CollectionName cn = clazz.getAnnotation(CollectionName.class);
			if(cn.value() == CollectionName.USE_CLASS_NAME) {
				return clazz.getName();
			}
			if(cn.value() == CollectionName.USE_PACKAGE_AND_CLASS_NAME) {
				return clazz.getCanonicalName();
			}
			return cn.value();
		}
		return clazz.getCanonicalName();
	}

}
