package com.cinefms.dbstore.utils.mongo;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MongoService {

	private static final Log log = LogFactory.getLog(MongoService.class);

	private String dbName;
	private String hosts;
	private String username;
	private String password;
	private String authDb;
	private String authMethod;
	private boolean auth = false;

	@Autowired
	private MongoClient client;

	private Map<String, MongoDatabase> dbs = new HashMap<>();

	public MongoClient getClient() {
		if (client != null) return client;

		List<ServerAddress> servers = getServers();
		String strServers = servers.stream().map(Objects::toString).collect(Collectors.joining(","));

		MongoClientSettings.Builder builder = MongoClientSettings.builder()
				.uuidRepresentation(UuidRepresentation.JAVA_LEGACY)
				.applyToClusterSettings(it -> it.hosts(servers));

		if (auth) {
			MongoCredential mc = getCredentials();
			builder.credential(mc);
			log.info("Connecting to mongo servers " + strServers + " with credentials. Using mechanism: " + mc.getMechanism());

		} else {
			log.info("Connecting to mongo servers " + strServers + " without credentials.");
		}

		client = MongoClients.create(builder.build());

		return client;
	}

	private MongoCredential getCredentials() {
		String authDatabase = authDb == null ? dbName : authDb;

		if (!StringUtils.hasText(authMethod)) {
			if ("CR".equalsIgnoreCase(authMethod)) {
				return MongoCredential.createCredential(
						username,
						authDatabase,
						password.toCharArray()
				);
			}
			if ("SCRAM-SHA-1".equalsIgnoreCase(authMethod)) {
				return MongoCredential.createScramSha1Credential(
						username,
						authDatabase,
						password.toCharArray()
				);
			}
			if ("SCRAM-SHA-256".equalsIgnoreCase(authMethod)) {
				return MongoCredential.createScramSha256Credential(
						username,
						authDatabase,
						password.toCharArray()
				);
			}
			if ("PLAIN".equalsIgnoreCase(authMethod)) {
				return MongoCredential.createPlainCredential(
						username,
						authDatabase,
						password.toCharArray()
				);
			}
		}

		return MongoCredential.createCredential(username, authDatabase, password.toCharArray());
	}

	private List<ServerAddress> getServers() {
		List<ServerAddress> out = new ArrayList<>();
		for (String uri : getHosts().split(",")) {
			String[] h = uri.split(":");
			out.add(new ServerAddress(h[0], h.length > 1 ? Integer.parseInt(h[1]) : ServerAddress.defaultPort()));
		}
		return out;
	}

	public MongoDatabase getDb() {
		return getDb(getDbName());
	}

	public MongoDatabase getDb(String db) {
		MongoDatabase out = dbs.get(db);
		if (out == null) {
			out = getClient().getDatabase(db);
			out.withReadPreference(ReadPreference.secondaryPreferred());
			out.withWriteConcern(WriteConcern.JOURNALED);
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
