package com.cinefms.dbstore.api.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cinefms.dbstore.api.OrderBy;
import com.cinefms.dbstore.api.Query;
import com.cinefms.dbstore.api.exceptions.MalformedQueryException;


public class BasicQuery implements Query {

	private List<Query> conditions = new ArrayList<Query>();
	private String key;
	private Object value;
	private OPERATOR operator;
	private COMPARATOR comparator;
	
	private List<OrderBy> orderBy = new ArrayList<OrderBy>();
	private int start=0;
	private int max=-1;
	
	
	private BasicQuery() {
	}
	
	private BasicQuery(List<Query> conditions, OPERATOR operator, String key, COMPARATOR comparator, Object value, List<OrderBy> orderBy, int start, int max) {
		super();
		this.conditions = conditions;
		this.operator = operator;
		this.key = key;
		this.comparator = comparator;
		this.value = value;
		this.orderBy = orderBy;
		this.start = start;
		this.max = max;
	}
	
	private BasicQuery(String key, COMPARATOR comparator, Object value) {
		this.key = key;
		this.comparator = comparator;
		this.value = value;
	}
	
	private BasicQuery(List<Query> conditions, OPERATOR operator) {
		this.conditions = conditions;
		this.operator = operator;
	}
	
	public OPERATOR getOperator() {
		return operator;
	}

	public List<Query> getNested() {
		return Collections.unmodifiableList(conditions);
	}

	public COMPARATOR getComparator() {
		return comparator;
	}

	public String getField() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	private Query add(String key, COMPARATOR c, Object value) throws MalformedQueryException {
		if(key == null) {
			throw new MalformedQueryException(MalformedQueryException.ERROR_CODE.KEY_MUST_NOT_BE_NULL);
		}
		List<Query> q = new ArrayList<Query>(getNested());
		q.add(new BasicQuery(key, c, value));
		return new BasicQuery(q,OPERATOR.AND);
	} 

	public Query in(String key, List<?> values) throws MalformedQueryException {
		return add(key,COMPARATOR.IN,values);
	}
	
	
	public Query in(String key, Object value) throws MalformedQueryException {
		return in(key, new Object[] { value });
	}
	
	
	public Query in(String key, Object[] values) throws MalformedQueryException {
		List<Object> os = new ArrayList<Object>();
		for(Object o : values) {
			os.add(o);
		}
		return in(key,os);
	}
	
	
	public Query contains(String key, String value) throws MalformedQueryException {
		return add(key, COMPARATOR.CONTAINS, value);
	}

	
	public Query eq(String key, Object value) throws MalformedQueryException {
		return add(key, COMPARATOR.EQ, value);
	}
	
	
	public Query lte(String key, Object value) throws MalformedQueryException {
		return add(key, COMPARATOR.LTE, value);
	}

	
	public Query lt(String key, Object value) throws MalformedQueryException {
		return add(key, COMPARATOR.LT, value);
	}

	
	public Query gte(String key, Object value) throws MalformedQueryException {
		return add(key, COMPARATOR.GTE, value);
	}

	
	public Query gt(String key, Object value) throws MalformedQueryException {
		return add(key, COMPARATOR.GT, value);
	}

	
	public Query ne(String key, Object value) throws MalformedQueryException {
		return add(key, COMPARATOR.NE, value);
	}

	
	public Query and(List<Query> queries) {
		List<Query> qp = new ArrayList<Query>(getNested());
		qp.add(new BasicQuery(queries,OPERATOR.AND));
		return new BasicQuery(qp,OPERATOR.AND);
	}	
	
	
	public Query and(Query... queries) {
		List<Query> task = new ArrayList<Query>();
		for(Query q : queries) {
			task.add(q);
		}
		return and(task);
	}

	
	public Query or(List<Query> queries) {
		List<Query> qp = new ArrayList<Query>(getNested());
		qp.add(new BasicQuery(queries,OPERATOR.OR));
		return new BasicQuery(qp,OPERATOR.AND);
	}
	
	
	public Query or(Query... queries) {
		List<Query> task = new ArrayList<Query>();
		for(Query q : queries) {
			task.add(q);
		}
		return or(task);
	}

	public static Query createQuery() {
		return new BasicQuery();
	}
	
	
	public Query order(String order, boolean asc) {
		List<OrderBy> ne = new ArrayList<OrderBy>(getOrderBy());
		ne.add(new OrderBy(order,asc));
		return new BasicQuery(getNested(),operator,key,comparator,value,ne,start,max);
	}

	
	public Query order(String order) {
		return order(order,true);
	}
	
	
	public Query start(int start) {
		return new BasicQuery(getNested(),operator,key,comparator,value,getOrderBy(),start,max);
	}

	
	public Query max(int max) {
		return new BasicQuery(getNested(),operator,key,comparator,value,getOrderBy(),start,max);
	}

	
	public List<OrderBy> getOrderBy() {
		return Collections.unmodifiableList(orderBy);
	}

	
	public int getStart() {
		return start;
	}

	
	public int getMax() {
		return max;
	}

	
	public String toString() {
		StringBuffer out = new StringBuffer();
		if(key!=null) {
			out.append(key);
			out.append(' ');
			switch (comparator) {
			case EQ:
				out.append("==");
				break;
			case CONTAINS:
				out.append("~");
				break;
			case LT:
				out.append("<");
				break;
			case LTE:
				out.append("<=");
				break;
			case GT:
				out.append(">");
				break;
			case GTE:
				out.append(">=");
				break;
			case NE:
				out.append("!=");
				break;
			default:
				break;
			}
			out.append(' ');
			out.append(value);
		} else {
			List<Query> n = getNested();
			out.append('(');
			for(Query fq : n) {
				if(n.indexOf(fq)>0) {
					switch (operator) {
					case AND:
						out.append(" AND ");
						break;
					case OR:
						out.append(" OR ");
						break;
					default:
						break;
					}
				}
				out.append(fq.toString());
			}
			out.append(')');
		}
		if(getOrderBy()!=null && getOrderBy().size()>0) {
			out.append(" ORDER BY ");
			for(OrderBy ob : getOrderBy()) {
				out.append('(');
				out.append(ob.getField());
				out.append(',');
				out.append(ob.isAsc()?"ASC":"DESC");
				out.append(')');
			}
		}
		if(start!=0 || max != -1) {
			out.append(" LIMIT ("+start+","+max+")");
		}
		return out.toString();
	}
	
	

	
}
