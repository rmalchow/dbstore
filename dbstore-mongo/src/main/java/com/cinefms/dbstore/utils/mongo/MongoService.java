package com.cinefms.dbstore.utils.mongo;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

public class MongoService {

	private String host="127.0.0.1";
	private int port=27017;
	private String dbName;

	private MongoClient client;
	private DB db;
	
	public MongoClient getClient() throws UnknownHostException {
		if(client == null) {
			client = new MongoClient(getHost(),getPort());
		}
		return client;
	}
	
	public DB getDb() throws UnknownHostException {
		if(this.db == null) {
			this.db = getClient().getDB(getDbName());
		}
		this.db.setWriteConcern(WriteConcern.ACKNOWLEDGED);
		return db;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getDbName() {
		return dbName==null?"tmp_"+Math.round(Math.random()*2933d):dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	
}
