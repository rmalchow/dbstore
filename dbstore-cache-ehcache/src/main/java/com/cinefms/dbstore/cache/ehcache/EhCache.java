package com.cinefms.dbstore.cache.ehcache;

import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import com.cinefms.dbstore.cache.api.DBStoreCache;

public class EhCache implements DBStoreCache {

	
	private Cache c;
	
	public EhCache(Cache c) {
		this.c = c;
	}

	public String getName() {
		return c.getName();
	}

	public void removeAll() {
		c.flush();
	}

	public void remove(String key) {
		c.remove(key);
	}

	public void put(String key, Object value) {
		Element e = new Element(key, value);
		c.put(e);
	}

	public <T> List<T> getList(String key, Class<? extends T> clazz) {
		Element e = c.get(key);
		if(e == null || e.getObjectValue()==null) {
			return null;
		}
 		return (List<T>)e.getObjectValue();
	}
 
	public <T> T get(String key, Class<? extends T> clazz) {
		Element e = c.get(key);
		if(e == null || e.getObjectValue()==null) {
			return null;
		}
 		return (T)e.getObjectValue();
	}

}
