package com.cinefms.dbstore.utils.mongo.utils;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class AssertCollection {

	public static <T> void assertContains(Collection<T> records, Consumer<T> matcher) {
		for (T record : records) {
			try {
				matcher.accept(record);
				return;
			} catch (AssertionError error) {

			}
		}

		throw new AssertionError("No match found");
	}

}
