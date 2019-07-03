package com.cinefms.dbstore.query.api;

import com.cinefms.dbstore.query.api.exceptions.MalformedQueryException;
import com.cinefms.dbstore.query.api.impl.OrderBy;

import java.util.List;

public interface DBStoreQuery {

	enum OPERATOR {
		AND, OR, NONE
	}

	enum COMPARATOR {
		CONTAINS, EQ, LTE, LT, GTE, GT, NE, NONE, IN, ALL, ELEM_MATCH
	}

	OPERATOR getOperator();

	List<DBStoreQuery> getNested();

	COMPARATOR getComparator();

	String getField();

	Object getValue();

	List<OrderBy> getOrderBy();

	int getStart();

	int getMax();

	DBStoreQuery in(String key, Object value) throws MalformedQueryException;

	DBStoreQuery in(String key, List<?> values) throws MalformedQueryException;

	DBStoreQuery in(String key, Object[] values) throws MalformedQueryException;

	DBStoreQuery all(String key, List<?> values) throws MalformedQueryException;

	DBStoreQuery all(String key, Object[] values) throws MalformedQueryException;

	DBStoreQuery contains(String key, String value) throws MalformedQueryException;

	DBStoreQuery eq(String key, Object value) throws MalformedQueryException;

	DBStoreQuery lte(String key, Object value) throws MalformedQueryException;

	DBStoreQuery lt(String key, Object value) throws MalformedQueryException;

	DBStoreQuery gte(String key, Object value) throws MalformedQueryException;

	DBStoreQuery gt(String key, Object value) throws MalformedQueryException;

	DBStoreQuery ne(String key, Object value) throws MalformedQueryException;

	DBStoreQuery elemMatch(String key, DBStoreQuery q);

	DBStoreQuery and(DBStoreQuery... queries);

	DBStoreQuery and(List<DBStoreQuery> queries);

	DBStoreQuery or(DBStoreQuery... queries);

	DBStoreQuery or(List<DBStoreQuery> queries);

	DBStoreQuery order(String order);

	DBStoreQuery order(String order, boolean asc);

	DBStoreQuery start(int start);

	DBStoreQuery max(int max);

}
