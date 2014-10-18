package com.cinefms.dbstore.cache.api;

public interface DBStoreCacheFactory {
	
	public abstract DBStoreCache getCache(String cachename);
	
}
