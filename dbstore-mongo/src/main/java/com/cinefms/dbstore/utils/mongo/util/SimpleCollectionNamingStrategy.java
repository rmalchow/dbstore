package com.cinefms.dbstore.utils.mongo.util;

import com.cinefms.dbstore.api.annotations.CollectionName;

import java.util.HashMap;
import java.util.Map;

public class SimpleCollectionNamingStrategy implements CollectionNamingStrategy {

	private final Map<Class<?>, String> names = new HashMap<>();

	@Override
	public String getCollectionName(Class<?> clazz) {
		String out = names.get(clazz);

		if (out == null) {
			if (clazz.getAnnotation(CollectionName.class) != null) {
				CollectionName cn = clazz.getAnnotation(CollectionName.class);

				if (cn.value().equals(CollectionName.USE_CLASS_NAME)) {
					out = clazz.getSimpleName();

				} else if (cn.value().equals(CollectionName.USE_PACKAGE_AND_CLASS_NAME)) {
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
