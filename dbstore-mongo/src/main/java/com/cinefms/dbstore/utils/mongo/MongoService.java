package com.cinefms.dbstore.utils.mongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

public class MongoService {

	private int port=27017;
	private String dbName;
	private String hosts;

	private MongoClient client;
	
	private Map<String,DB> dbs = new HashMap<String, DB>();
	
	public MongoClient getClient() throws UnknownHostException {
		if(client == null) {
			client = new MongoClient(getServers());
		}
		return client;
	}
	
	private List<ServerAddress> getServers() throws UnknownHostException {
		List<ServerAddress> out = new ArrayList<ServerAddress>();
		for(String h : getHosts().split(",")) {
			String[] x = h.split(":");
			String host = x[0];
			int port = this.port; 
			if(x.length>1) {
				port = Integer.parseInt(x[1]);
			}
			out.add(new ServerAddress(host, port));
		}
		return out;
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
	
	public String getDbName() {
		return dbName==null?"tmp_"+Math.round(Math.random()*2933d):dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getHosts() {
		return hosts;
	}

	public void setHosts(String hosts) {
		this.hosts = hosts;
	}

	
}
