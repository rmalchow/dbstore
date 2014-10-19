package com.cinefms.dbstore.redis;

import org.redisson.Config;
import org.redisson.Redisson;
import org.springframework.beans.factory.FactoryBean;

public class RedissonFactory implements FactoryBean<Redisson> {

	private boolean singleton = true;
	private String singleServer;
	private String auth;

	public Redisson getObject() throws Exception {
		Config config = new Config();
		config.useSingleServer().setAddress(singleServer).setRetryAttempts(3);
		Redisson redisson = Redisson.create(config);
		return redisson;
	}

	public Class<?> getObjectType() {
		return Redisson.class;
	}

	public boolean isSingleton() {
		return this.singleton;
	}

	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	public String getSingleServer() {
		return singleServer;
	}

	public void setSingleServer(String singleServer) {
		this.singleServer = singleServer;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public static void main(String[] args) {

	}

}
