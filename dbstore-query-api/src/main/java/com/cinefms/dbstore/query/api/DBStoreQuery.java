package com.cinefms.dbstore.query.api;

import com.cinefms.dbstore.query.api.impl.OrderBy;

import java.util.List;

public interface DBStoreQuery {

	OPERATOR getOperator();

	List<DBStoreQuery> getNested();

	COMPARATOR getComparator();

	String getField();

	Object getValue();

	List<OrderBy> getOrderBy();

	int getStart();

	int getMax();

	DBStoreQuery in(String key, Object... values);

	DBStoreQuery in(String key, List<?> values);

	DBStoreQuery nin(String key, Object... values);

	DBStoreQuery nin(String key, List<?> values);

	DBStoreQuery exists(String key);

	DBStoreQuery all(String key, Object... values);

	DBStoreQuery all(String key, List<?> values);

	DBStoreQuery contains(String key, String value);

	DBStoreQuery eq(String key, Object value);

	DBStoreQuery lte(String key, Object value);

	DBStoreQuery lt(String key, Object value);

	DBStoreQuery gte(String key, Object value);

	DBStoreQuery gt(String key, Object value);

	DBStoreQuery ne(String key, Object value);

	DBStoreQuery elemMatch(String key, DBStoreQuery q);

	DBStoreQuery and(DBStoreQuery... queries);

	DBStoreQuery and(List<DBStoreQuery> queries);

	DBStoreQuery or(DBStoreQuery... queries);

	DBStoreQuery or(List<DBStoreQuery> queries);

	DBStoreQuery order(String order);

	DBStoreQuery order(String order, boolean asc);

	DBStoreQuery order(OrderBy... orders);

	DBStoreQuery start(int start);

	DBStoreQuery max(int max);

	enum OPERATOR {
		AND, OR, NONE
	}

	enum COMPARATOR {
		CONTAINS, EQ, LTE, LT, GTE, GT, NE, NONE, IN, NIN, EXISTS, ALL, ELEM_MATCH
	}

}
