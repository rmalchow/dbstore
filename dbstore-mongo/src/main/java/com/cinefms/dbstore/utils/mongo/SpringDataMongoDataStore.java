package com.cinefms.dbstore.utils.mongo;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.DB;
import com.mongodb.Mongo;

public class SpringDataMongoDataStore extends AMongoDataStore {
	
	@Autowired
	private Mongo mongo;

	@Override
	public DB getDB(String db) throws UnknownHostException {
		db = db==null?defaultDb:(dbPrefix==null?"":(dbPrefix+"_"))+db;
		return mongo.getDB(db);
	}

	
}
