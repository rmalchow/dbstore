package com.cinefms.dbstore.cache.memcached;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.ConnectionFactoryBuilder.Locator;
import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
import net.spy.memcached.ConnectionObserver;
import net.spy.memcached.DefaultHashAlgorithm;
import net.spy.memcached.FailureMode;
import net.spy.memcached.HashAlgorithm;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.OperationFactory;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.ops.OperationQueueFactory;
import net.spy.memcached.transcoders.Transcoder;

import org.redisson.Redisson;

import com.cinefms.dbstore.cache.api.DBStoreCache;
import com.cinefms.dbstore.cache.api.DBStoreCacheFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MemcacheCacheFactory implements DBStoreCacheFactory {

	private final ConnectionFactoryBuilder connectionFactoryBuilder = new ConnectionFactoryBuilder();

	private MemcachedClient memcachedClient;
	private Redisson redisson;
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private List<InetSocketAddress> addresses;

	private Map<String, DBStoreCache> caches = new HashMap<String, DBStoreCache>();

	private MemcachedClient getClient() {
		if (memcachedClient == null) {
			try {
				memcachedClient = new MemcachedClient(connectionFactoryBuilder.build(), addresses);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return memcachedClient;
	}

	public DBStoreCache getCache(String cachename) {
		DBStoreCache out = caches.get(cachename);
		if (out == null) {
			out = new MemcacheCache(getClient(), cachename, redisson, objectMapper);
			caches.put(cachename, out);
		}
		return out;
	}

	public Redisson getRedisson() {
		return redisson;
	}

	public void setRedisson(Redisson redisson) {
		this.redisson = redisson;
	}

	public void setServers(String servers) {
		this.addresses = AddrUtil.getAddresses(servers);
	}

	public void setAuthDescriptor(final AuthDescriptor to) {
		connectionFactoryBuilder.setAuthDescriptor(to);
	}

	public void setDaemon(final boolean d) {
		connectionFactoryBuilder.setDaemon(d);
	}

	public void setFailureMode(final FailureMode fm) {
		connectionFactoryBuilder.setFailureMode(fm);
	}

	public void setHashAlg(final String to) {
		HashAlgorithm h = DefaultHashAlgorithm.valueOf(to); 
		connectionFactoryBuilder.setHashAlg(h);
	}

	public void setInitialObservers(final Collection<ConnectionObserver> obs) {
		connectionFactoryBuilder.setInitialObservers(obs);
	}

	public void setLocatorType(final Locator l) {
		connectionFactoryBuilder.setLocatorType(l);
	}

	public void setMaxReconnectDelay(final long to) {
		connectionFactoryBuilder.setMaxReconnectDelay(to);
	}

	public void setOpFact(final OperationFactory f) {
		connectionFactoryBuilder.setOpFact(f);
	}

	public void setOpQueueFactory(final OperationQueueFactory q) {
		connectionFactoryBuilder.setOpQueueFactory(q);
	}

	public void setOpQueueMaxBlockTime(final long t) {
		connectionFactoryBuilder.setOpQueueMaxBlockTime(t);
	}

	public void setOpTimeout(final long t) {
		connectionFactoryBuilder.setOpTimeout(t);
	}

	public void setProtocol(final Protocol prot) {
		connectionFactoryBuilder.setProtocol(prot);
	}

	public void setReadBufferSize(final int to) {
		connectionFactoryBuilder.setReadBufferSize(to);
	}

	public void setReadOpQueueFactory(final OperationQueueFactory q) {
		connectionFactoryBuilder.setReadOpQueueFactory(q);
	}

	public void setShouldOptimize(final boolean o) {
		connectionFactoryBuilder.setShouldOptimize(o);
	}

	public void setTimeoutExceptionThreshold(final int to) {
		connectionFactoryBuilder.setTimeoutExceptionThreshold(to);
	}

	public void setTranscoder(final Transcoder<Object> t) {
		connectionFactoryBuilder.setTranscoder(t);
	}

	public void setUseNagleAlgorithm(final boolean to) {
		connectionFactoryBuilder.setUseNagleAlgorithm(to);
	}

	public void setWriteOpQueueFactory(final OperationQueueFactory q) {
		connectionFactoryBuilder.setWriteOpQueueFactory(q);
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

}
