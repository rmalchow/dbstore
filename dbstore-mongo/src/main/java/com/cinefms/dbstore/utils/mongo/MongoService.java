package com.cinefms.dbstore.utils.mongo;

import com.mongodb.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MongoService {

	private static Log log = LogFactory.getLog(MongoService.class);

	public static final int DEFAULT_PORT = 27017;

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

	public MongoClient getClient() {
		if (client != null) return client;

		List<ServerAddress> servers = getServers();

		if (servers.size() == 1) {
			ServerAddress server = servers.get(0);

			if (auth) {
				MongoCredential mc = getCredentials();
				log.info("Connecting to mongo server " + server + " with credentials. Using mechanism: " + mc.getMechanism());
				client = new MongoClient(server, mc, MongoClientOptions.builder().build());

			} else {
				log.info("Connecting to mongo server " + server + " without credentials.");
				client = new MongoClient(server);
			}

		} else {
			String strServers = servers.stream().map(Objects::toString).collect(Collectors.joining(","));

			if (auth) {
				MongoCredential mc = getCredentials();
				log.info("Connecting to mongo servers " + strServers + " with credentials. Using mechanism: " + mc.getMechanism());
				client = new MongoClient(servers, mc, MongoClientOptions.builder().build());

			} else {
				log.info("Connecting to mongo servers " + strServers + " without credentials.");
				client = new MongoClient(servers);
			}
		}

		return client;
	}

	private MongoCredential getCredentials() {
		if (!StringUtils.isEmpty(authMethod)) {
			if (getAuthMethod().compareTo("CR") == 0) {
				return MongoCredential.createMongoCRCredential(username, authDb, password.toCharArray());
			}
			if (getAuthMethod().compareTo("SCRAM-SHA-1") == 0) {
				return MongoCredential.createScramSha1Credential(username, authDb == null ? dbName : authDb, password.toCharArray());
			}
		}
		return MongoCredential.createCredential(username, authDb == null ? dbName : authDb, password.toCharArray());
	}

	private List<ServerAddress> getServers() {
		List<ServerAddress> out = new ArrayList<>();
		for (String uri : getHosts().split(",")) {
			String[] h = uri.split(":");
			out.add(new ServerAddress(h[0], h.length > 1 ? Integer.parseInt(h[1]) : this.DEFAULT_PORT));
		}
		return out;
	}

	public DB getDb() {
		return getDb(getDbName());
	}

	public DB getDb(String db) {
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
