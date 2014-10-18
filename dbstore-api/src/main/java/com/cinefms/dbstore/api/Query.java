package com.cinefms.dbstore.api;

import java.util.List;

import com.cinefms.dbstore.api.exceptions.MalformedQueryException;

public interface Query {

	public static enum OPERATOR {
		AND, OR, NONE
	};
	
	public static enum COMPARATOR {
		CONTAINS, EQ, LTE, LT, GTE, GT, NE, NONE, IN
	};

	
	
	public OPERATOR getOperator(); 
	public List<Query> getNested(); 


	public COMPARATOR getComparator(); 
	public String getField();
	public Object getValue();

	public List<OrderBy> getOrderBy();
	public int getStart();
	public int getMax();
	

	public Query in(String key, Object value) throws MalformedQueryException;
	public Query in(String key, List<?> values) throws MalformedQueryException;
	public Query in(String key, Object[] values) throws MalformedQueryException;
	
	public Query contains(String key, String value) throws MalformedQueryException;
	
	public Query eq(String key, Object value) throws MalformedQueryException;
	public Query lte(String key, Object value) throws MalformedQueryException;
	public Query lt(String key, Object value) throws MalformedQueryException;
	public Query gte(String key, Object value) throws MalformedQueryException;
	public Query gt(String key, Object value) throws MalformedQueryException;
	public Query ne(String key, Object value) throws MalformedQueryException;

	public Query and(Query... queries);
	public Query and(List<Query> queries);
	public Query or(Query... queries);
	public Query or(List<Query> queries);
	
	public Query order(String order);
	public Query order(String order, boolean asc);
	public Query start(int start);
	public Query max(int max);
	
	
}
