package com.cinefms.dbstore.utils.mongo;

import org.bson.types.ObjectId;

public class BsonGuidGenerator {

	public String getGuid() {
		return ObjectId.get().toString();
	}

}
