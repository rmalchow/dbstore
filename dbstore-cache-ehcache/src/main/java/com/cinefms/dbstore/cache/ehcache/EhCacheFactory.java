package com.cinefms.dbstore.cache.ehcache;

import javax.annotation.PostConstruct;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import com.cinefms.dbstore.cache.api.DBStoreCache;
import com.cinefms.dbstore.cache.api.DBStoreCacheFactory;

public class EhCacheFactory  implements DBStoreCacheFactory {

	private CacheManager cacheManager ;
	
	private int timeToLiveSeconds = 3600;
	private int timeToIdleSeconds = 3600;
	private int maxElementsInMemory = 10000;

	public DBStoreCache getCache(String cachename) {
		CacheManager cm = getCacheManager();
		Cache c = cm.getCache(cachename);
		if(c==null) {
			try {
				c = new Cache(cachename, maxElementsInMemory, false, false, timeToLiveSeconds, timeToIdleSeconds);
				try {
					cm.addCache(c);
				} catch (Exception e) {
				}
				c = cm.getCache(cachename);
			} catch (Exception e) {
				return null;
			}
		}
		return new EhCache(c);
	}

	private CacheManager getCacheManager() {
		if(cacheManager==null) {
			cacheManager = CacheManager.create();
		}
		return cacheManager;
	}
	
	@PostConstruct
	public void init() {
		cacheManager = CacheManager.create();
		
	}
	
	
}
