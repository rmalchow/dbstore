package com.cinefms.dbstore.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.codec.RedissonCodec;
import org.springframework.beans.factory.FactoryBean;

import com.cinefms.dbstore.redis.util.LenientJsonCodec;

public class RedissonFactory implements FactoryBean<Redisson> {

	private static Log log = LogFactory.getLog(RedissonFactory.class);
	
	private boolean singleton = true;
	private String singleServer;
	private String auth;
	private RedissonCodec codec = new LenientJsonCodec(); 

	public Redisson getObject() throws Exception {
		log.info("### REDISSON FACTORY - CREATING ... ");
		Config config = new Config();
		config.useSingleServer().setAddress(singleServer).setRetryAttempts(3);
		config.setCodec(codec);
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

	public RedissonCodec getCodec() {
		return codec;
	}

	public void setCodec(RedissonCodec codec) {
		this.codec = codec;
	}

}
