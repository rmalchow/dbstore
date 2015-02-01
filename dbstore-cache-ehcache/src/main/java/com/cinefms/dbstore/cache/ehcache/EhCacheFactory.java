package com.cinefms.dbstore.cache.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import com.cinefms.dbstore.cache.api.DBStoreCache;
import com.cinefms.dbstore.cache.api.DBStoreCacheFactory;

public class EhCacheFactory  implements DBStoreCacheFactory {

	private CacheManager cacheManager;
	
	private int timeToLiveSeconds = 3600;
	private int timeToIdleSeconds = 3600;
	private int maxElementsInMemory = 10000;

	public DBStoreCache getCache(String cachename) {
		Cache c = cacheManager.getCache(cachename);
		if(c==null) {
			try {
				c = new Cache(cachename, maxElementsInMemory, false, false, timeToLiveSeconds, timeToIdleSeconds);
				try {
					cacheManager.addCache(c);
				} catch (Exception e) {
				}
				c = cacheManager.getCache(cachename);
			} catch (Exception e) {
				return null;
			}
		}
		return new EhCache(c);
	}

	public void init() {
		cacheManager = CacheManager.create();
		
	}
	
	
}
