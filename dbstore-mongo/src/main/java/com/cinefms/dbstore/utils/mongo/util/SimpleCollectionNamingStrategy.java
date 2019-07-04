package com.cinefms.dbstore.utils.mongo.util;

import com.cinefms.dbstore.api.annotations.CollectionName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

public class SimpleCollectionNamingStrategy implements CollectionNamingStrategy {

	private static Log log = LogFactory.getLog(SimpleCollectionNamingStrategy.class);

	private Map<Class, String> names = new HashMap<>();

	@Override
	public String getCollectionName(Class<?> clazz) {
		String out = names.get(clazz);
		if (out == null) {
			if (clazz.getAnnotation(CollectionName.class) != null) {
				CollectionName cn = clazz.getAnnotation(CollectionName.class);
				if (cn.value() == CollectionName.USE_CLASS_NAME) {
					out = clazz.getName();
				} else if (cn.value() == CollectionName.USE_PACKAGE_AND_CLASS_NAME) {
					out = clazz.getCanonicalName();
				} else {
					out = cn.value();
				}
			} else {
				out = clazz.getCanonicalName();
			}
			names.put(clazz, out);
		}
		return out;
	}

}
