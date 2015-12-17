package com.cinefms.dbstore.utils.mongo;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.springframework.cglib.beans.BeanMap;

import com.cinefms.dbstore.api.DataStore;
import com.cinefms.dbstore.utils.mongo.util.CollectionNamingStrategy;
import com.cinefms.dbstore.utils.mongo.util.SimpleCollectionNamingStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MongoDataStore extends AMongoDataStore implements DataStore {

	private CollectionNamingStrategy collectionNamingStrategy = new SimpleCollectionNamingStrategy();
	private boolean checkUpdates = false;
	private ObjectMapper objectMapper;
	
	public String getCollectionName(Class<?> clazz) {
		return collectionNamingStrategy.getCollectionName(clazz);
	}

	public CollectionNamingStrategy getCollectionNamingStrategy() {
		return collectionNamingStrategy;
	}

	public void setCollectionNamingStrategy(CollectionNamingStrategy collectionNamingStrategy) {
		this.collectionNamingStrategy = collectionNamingStrategy;
	}

	public ObjectMapper getObjectMapper() {
		if(objectMapper==null) {
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}
	
	@Override
	public <T> boolean needsUpdate(T old, T object) {
		if(!checkUpdates) {
			return true;
		}
		if(old==null || object==null) {
			return true;
		}
		try {
			ObjectMapper om = getObjectMapper();
			byte[] o1 = om.writeValueAsBytes(old);
			byte[] o2 = om.writeValueAsBytes(object);
			
			if(o1.length!=o2.length) {
				return true;
			}
			for(int i=0;i<o1.length;i++) {
				if(o1[i]!=o2[i]) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			return true;
		}
	}

}
