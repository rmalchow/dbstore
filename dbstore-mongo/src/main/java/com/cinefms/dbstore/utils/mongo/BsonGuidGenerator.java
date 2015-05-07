package com.cinefms.dbstore.utils.mongo;

import org.bson.types.ObjectId;

import com.cinefms.dbstore.utils.mongo.util.CollectionNamingStrategy;

public class BsonGuidGenerator {

	public String getGuid() {
		return ObjectId.get().toString();
	}

}
