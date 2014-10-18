package com.cinefms.dbstore.cache.api;

import java.util.List;

public interface DBStoreCache {

	public abstract String getName();

	public abstract void removeAll();

	public abstract void remove(String key);

	public abstract void put(String key, Object value);

	public abstract <T> List<T> getList(String key, Class<? extends T> clazz);

	public abstract <T> T get(String key, Class<? extends T> clazz);

}
