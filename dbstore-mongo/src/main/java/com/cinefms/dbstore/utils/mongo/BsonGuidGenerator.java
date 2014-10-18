package com.cinefms.dbstore.utils.mongo;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
public class BsonGuidGenerator {

	public String getGuid() {
		return ObjectId.get().toString();
	}

}
