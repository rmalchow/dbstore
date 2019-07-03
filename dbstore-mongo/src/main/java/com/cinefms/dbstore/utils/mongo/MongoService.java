package com.cinefms.dbstore.utils.mongo;

import com.mongodb.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;
import java.util.*;

@Service
public class MongoService {

	private static Log log = LogFactory.getLog(MongoService.class);

	private int port = 27017;
	private String dbName;
	private String hosts;
	private String username;
	private String password;
	private String authDb;
	private String authMethod;
	private boolean auth = false;

	@Autowired
	private MongoClient client;

	private Map<String, DB> dbs = new HashMap<>();

	public MongoClient getClient() throws UnknownHostException {
		if (client == null) {
			List<ServerAddress> servers = getServers();
			log.info("##############################################################");
			log.info("##  ");
			log.info("##  CONNECTING A NEW CLIENT!");
			if (servers.size() == 1) {
				log.info("##  ");
				log.info("##  SINGLE-SERVER!");
				log.info("##  ---------------------------");
				if (auth) {
					MongoCredential mc;
					authMethod = authMethod == null ? "" : authMethod;
					if (getAuthMethod().compareTo("CR") == 0) {
						mc = MongoCredential.createMongoCRCredential(username, authDb, password.toCharArray());
					} else if (getAuthMethod().compareTo("SCRAM-SHA-1") == 0) {
						mc = MongoCredential.createScramSha1Credential(username, authDb == null ? dbName : authDb, password.toCharArray());
					} else {
						mc = MongoCredential.createCredential(username, authDb == null ? dbName : authDb, password.toCharArray());
					}
					log.info("##  ");
					log.info("##  WITH CREDENTIALS");
					log.info("##  --------------------------- MECHANISM: " + mc.getMechanism());
					client = new MongoClient(servers.get(0), Collections.singletonList(mc));
				} else {
					log.info("##  ");
					log.info("##  WITHOUT CREDENTIALS");
					log.info("##  ---------------------------");
					client = new MongoClient(servers.get(0));
				}
			} else {
				log.info("##  ");
				log.info("##  SERVERS: " + servers.size());
				log.info("##  ---------------------------");
				for (ServerAddress server : servers) {
					log.info("##  " + server.getHost() + ":" + server.getPort());
				}
				log.info("##  ---------------------------");
				if (auth) {
					log.info("##  ");
					log.info("##  WITH CREDENTIALS");
					log.info("##  ---------------------------");
					MongoCredential mc = MongoCredential.createCredential(username, dbName, password.toCharArray());
					log.info("##  --------------------------- MECHANISM: " + mc.getMechanism());
					List<MongoCredential> mcs = new ArrayList<>();
					mcs.add(mc);
					client = new MongoClient(servers, mcs);
				} else {
					log.info("##  ");
					log.info("##  WITHOUT CREDENTIALS");
					log.info("##  ---------------------------");
					client = new MongoClient(servers);
				}
			}
			log.info("##  ");
			log.info("##  ");
			log.info("##############################################################");
		}
		return client;
	}

	private List<ServerAddress> getServers() {
		List<ServerAddress> out = new ArrayList<>();
		for (String h : getHosts().split(",")) {
			String[] x = h.split(":");
			String host = x[0];
			int port = this.port;
			if (x.length > 1) {
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
		if (out == null) {
			out = getClient().getDB(db);
			out.setReadPreference(ReadPreference.secondaryPreferred());
			out.setWriteConcern(WriteConcern.JOURNALED);
			dbs.put(db, out);
		}
		return out;
	}

	public String getDbName() {
		return dbName == null ? "tmp_" + Math.round(Math.random() * 2933d) : dbName;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAuth() {
		return auth;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	public String getAuthDb() {
		return authDb;
	}

	public void setAuthDb(String authDb) {
		this.authDb = authDb;
	}

	public String getAuthMethod() {
		return authMethod;
	}

	public void setAuthMethod(String authMethod) {
		this.authMethod = authMethod;
	}

}
