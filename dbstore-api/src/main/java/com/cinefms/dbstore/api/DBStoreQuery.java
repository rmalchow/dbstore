package com.cinefms.dbstore.api;

import java.util.List;

import com.cinefms.dbstore.api.exceptions.MalformedQueryException;

public interface DBStoreQuery {

	public static enum OPERATOR {
		AND, OR, NONE
	};
	
	public static enum COMPARATOR {
		CONTAINS, EQ, LTE, LT, GTE, GT, NE, NONE, IN, ELEM_MATCH
	};

	public OPERATOR getOperator(); 
	public List<DBStoreQuery> getNested(); 


	public COMPARATOR getComparator(); 
	public String getField();
	public Object getValue();

	public List<OrderBy> getOrderBy();
	public int getStart();
	public int getMax();
	

	public DBStoreQuery in(String key, Object value) throws MalformedQueryException;
	public DBStoreQuery in(String key, List<?> values) throws MalformedQueryException;
	public DBStoreQuery in(String key, Object[] values) throws MalformedQueryException;
	
	public DBStoreQuery contains(String key, String value) throws MalformedQueryException;
	
	public DBStoreQuery eq(String key, Object value) throws MalformedQueryException;
	public DBStoreQuery lte(String key, Object value) throws MalformedQueryException;
	public DBStoreQuery lt(String key, Object value) throws MalformedQueryException;
	public DBStoreQuery gte(String key, Object value) throws MalformedQueryException;
	public DBStoreQuery gt(String key, Object value) throws MalformedQueryException;
	public DBStoreQuery ne(String key, Object value) throws MalformedQueryException;
	public DBStoreQuery elemMatch(String key, DBStoreQuery q);

	public DBStoreQuery and(DBStoreQuery... queries);
	public DBStoreQuery and(List<DBStoreQuery> queries);
	public DBStoreQuery or(DBStoreQuery... queries);
	public DBStoreQuery or(List<DBStoreQuery> queries);
	
	public DBStoreQuery order(String order);
	public DBStoreQuery order(String order, boolean asc);
	public DBStoreQuery start(int start);
	public DBStoreQuery max(int max);

	
	
	
	
}
