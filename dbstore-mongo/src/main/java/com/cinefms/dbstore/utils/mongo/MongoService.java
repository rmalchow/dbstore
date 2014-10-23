package com.cinefms.dbstore.utils.mongo;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

public class MongoService {

	private String host="127.0.0.1";
	private int port=27017;
	private String dbName;

	private MongoClient client;
	
	private Map<String,DB> dbs = new HashMap<String, DB>();
	
	public MongoClient getClient() throws UnknownHostException {
		if(client == null) {
			client = new MongoClient(getHost(),getPort());
		}
		return client;
	}
	
	public DB getDb() throws UnknownHostException {
		return getDb(getDbName());
	}

	public DB getDb(String db) throws UnknownHostException {
		DB out = dbs.get(db);
		if(out == null) {
			out = getClient().getDB(db);
			out.setWriteConcern(WriteConcern.ACKNOWLEDGED);
			dbs.put(db, out);
		}
		return out;
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
