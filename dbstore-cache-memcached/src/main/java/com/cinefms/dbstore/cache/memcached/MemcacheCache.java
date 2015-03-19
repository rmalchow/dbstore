package com.cinefms.dbstore.cache.memcached;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.spy.memcached.MemcachedClient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.redisson.Redisson;
import org.redisson.core.MessageListener;
import org.redisson.core.RAtomicLong;
import org.redisson.core.RTopic;

import com.cinefms.dbstore.cache.api.DBStoreCache;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MemcacheCache implements DBStoreCache, MessageListener<CacheUpdate> {
	
	private static Log log = LogFactory.getLog(MemcacheCache.class);

	private Redisson redisson;
	private ObjectMapper mapper;
	
	private MemcachedClient memcachedClient;
	private String namespace;
	private Long generation;
	private RAtomicLong atomicLong;
	private RTopic<CacheUpdate> topic;
	private int time;
	
	public MemcacheCache(MemcachedClient client, String cachename, Redisson redisson, ObjectMapper mapper) {
		this.memcachedClient = client;
		this.namespace = cachename;
		this.redisson = redisson;
		this.mapper = mapper;
		this.topic = redisson.getTopic("_cache:"+namespace+"_updates");
		this.topic.addListener(this);
		this.atomicLong = redisson.getAtomicLong("_cache:"+namespace+"_generation");
		log.info("created memcached cache with namespace: "+namespace+", generation: "+getGeneration());
	}

	public String getName() {
		return namespace;
	}
	
	public void onMessage(CacheUpdate update) {
		log.debug(" ####################################################" );
		log.debug(" ### cache update received for: "+update.getNamespace()+" / "+update.getNewGeneration());
		log.debug(" ####################################################" );
		if(this.generation.longValue()!=update.getNewGeneration().longValue()) {
			this.generation = update.getNewGeneration();
		}
	}
	
	public long getGeneration() {
		return generation.longValue();
	}

	public void removeAll() {
		generation = atomicLong.incrementAndGet();
		CacheUpdate cu = new CacheUpdate();
		cu.setNamespace(namespace);
		cu.setNewGeneration(generation);
		topic.publish(cu);
	}

	public void remove(String key) {
		key = namespace+":"+getGeneration()+":"+key;
		Future<Boolean> f=memcachedClient.delete(key);
		try {
			f.get(3, TimeUnit.SECONDS);
		} catch(InterruptedException e) {
			f.cancel(false);
		} catch(ExecutionException e) {
			f.cancel(false);
		} catch(TimeoutException e) {
		    f.cancel(false);
		}
	}
	
	public void put(String key, Object value) {
		key = namespace+":"+getGeneration()+":"+key;
		Future<Boolean> f = null;
		try {
			f = memcachedClient.add(key, time, mapper.writeValueAsString(value));
			f.get(3, TimeUnit.SECONDS);
		} catch (Exception e) {
			if(f!=null) {
				try {
					f.cancel(false);
				} catch (Exception e2) {
				}
			}
			log.warn("json mapping failed: "+value,e);
		}
	}

	public <T> List<T> getList(String key, Class<? extends T> clazz) {
		key = namespace+":"+getGeneration()+":"+key;
		String v = get(key);
		if(v==null) {
			log.debug("MEMCACHED CACHE: "+clazz.getCanonicalName()+":"+key+" not found ... ");
			return null;
		}
		try {
			JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
			List<T> out = mapper.readValue(v, type);
			log.debug("MEMCACHED CACHE: "+clazz.getCanonicalName()+":"+key+" FOUND: "+v);
			return out;
		} catch (Exception e) {
			log.warn("json mapping failed: List<"+clazz.getCanonicalName()+">",e);
			return null;
		}
	}

	public <T> T get(String key, Class<? extends T> clazz) {
		key = namespace+":"+getGeneration()+":"+key;
		String v = get(key);
		if(v==null) {
			log.debug("MEMCACHED CACHE: "+clazz.getCanonicalName()+":"+key+" not found ... ");
			return null;
		}
		try {
			T out = mapper.readValue(v, clazz);
			log.debug("MEMCACHED CACHE: "+clazz.getCanonicalName()+":"+key+" FOUND: "+v);
			return out;
		} catch (Exception e) {
			log.warn("json mapping failed: List<"+clazz.getCanonicalName()+">",e);
			return null;
		}
	}
	
	public String get(String key) {
		Future<Object> f=memcachedClient.asyncGet(key);
		try {
			Object o =  f.get(1, TimeUnit.SECONDS);
			if(o!=null) {
				return (String)o;
			}
		} catch(InterruptedException e) {
			f.cancel(false);
		} catch(ExecutionException e) {
			f.cancel(false);
		} catch(TimeoutException e) {
		    f.cancel(false);
		}
		return null;
		
	}

}
