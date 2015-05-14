package com.cinefms.dbstore.cache.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EhCacheInstanceTest {

	@Test
	public void testEhCache() {
		System.err.println("hello ... ");
		
		CacheManager cacheManager = CacheManager.create();
		Cache c = new Cache("aaa", 10, false, false, 1000, 1000);
		cacheManager.addCache(c);
		{
			StringBuffer sb = new StringBuffer();
			Element e = new Element("x", sb);
			c.put(e);
		}
		StringBuffer sb1 = (StringBuffer)c.get("x").getValue();
		sb1.append("aaa");
		StringBuffer sb2 = (StringBuffer)c.get("x").getValue();
		sb1.append("bbb");
		
		System.err.println("stringbuffer: "+sb1+" / "+sb2);
		System.err.println(sb1);
		System.err.println(sb2);
	}
	
}
