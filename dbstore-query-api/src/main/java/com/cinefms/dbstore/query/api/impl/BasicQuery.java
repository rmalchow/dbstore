package com.cinefms.dbstore.query.api.impl;

import com.cinefms.dbstore.query.api.DBStoreQuery;
import com.cinefms.dbstore.query.api.exceptions.MalformedQueryException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BasicQuery implements DBStoreQuery {

	private List<DBStoreQuery> conditions = new ArrayList<>();
	private String key;
	private Object value;
	private OPERATOR operator;
	private COMPARATOR comparator;

	private List<OrderBy> orderBy = new ArrayList<>();
	private int start = 0;
	private int max = -1;


	private BasicQuery() {
	}

	private BasicQuery(List<DBStoreQuery> conditions, OPERATOR operator, String key, COMPARATOR comparator, Object value, List<OrderBy> orderBy, int start, int max) {
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

	private BasicQuery(List<DBStoreQuery> conditions, OPERATOR operator) {
		this.conditions = conditions;
		this.operator = operator;
	}

	public static DBStoreQuery createQuery() {
		return new BasicQuery();
	}

	public OPERATOR getOperator() {
		return operator;
	}

	public List<DBStoreQuery> getNested() {
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

	private DBStoreQuery add(String key, COMPARATOR c, Object value) throws MalformedQueryException {
		if (key == null) {
			throw new MalformedQueryException(MalformedQueryException.ERROR_CODE.KEY_MUST_NOT_BE_NULL);
		}
		List<DBStoreQuery> q = new ArrayList<>(getNested());
		q.add(new BasicQuery(key, c, value));
		return new BasicQuery(q, OPERATOR.AND);
	}

	public DBStoreQuery in(String key, Object value) throws MalformedQueryException {
		return in(key, new Object[]{value});
	}

	public DBStoreQuery in(String key, List<?> values) throws MalformedQueryException {
		return add(key, COMPARATOR.IN, values);
	}

	public DBStoreQuery in(String key, Object[] values) throws MalformedQueryException {
		List<Object> os = new ArrayList<>();
		for (Object o : values) {
			os.add(o);
		}
		return in(key, os);
	}

	public DBStoreQuery all(String key, List<?> values) throws MalformedQueryException {
		return add(key, COMPARATOR.ALL, values);
	}

	public DBStoreQuery all(String key, Object[] values) throws MalformedQueryException {
		List<Object> os = new ArrayList<>();
		for (Object o : values) {
			os.add(o);
		}
		return in(key, os);
	}

	public DBStoreQuery contains(String key, String value) throws MalformedQueryException {
		return add(key, COMPARATOR.CONTAINS, value);
	}

	public DBStoreQuery eq(String key, Object value) throws MalformedQueryException {
		return add(key, COMPARATOR.EQ, value);
	}

	public DBStoreQuery lte(String key, Object value) throws MalformedQueryException {
		return add(key, COMPARATOR.LTE, value);
	}

	public DBStoreQuery lt(String key, Object value) throws MalformedQueryException {
		return add(key, COMPARATOR.LT, value);
	}

	public DBStoreQuery gte(String key, Object value) throws MalformedQueryException {
		return add(key, COMPARATOR.GTE, value);
	}

	public DBStoreQuery gt(String key, Object value) throws MalformedQueryException {
		return add(key, COMPARATOR.GT, value);
	}

	public DBStoreQuery ne(String key, Object value) throws MalformedQueryException {
		return add(key, COMPARATOR.NE, value);
	}

	public DBStoreQuery and(List<DBStoreQuery> queries) {
		List<DBStoreQuery> qp = new ArrayList<>(getNested());
		qp.add(new BasicQuery(queries, OPERATOR.AND));
		return new BasicQuery(qp, OPERATOR.AND);
	}

	public DBStoreQuery and(DBStoreQuery... queries) {
		List<DBStoreQuery> task = new ArrayList<>();
		for (DBStoreQuery q : queries) {
			task.add(q);
		}
		return and(task);
	}

	public DBStoreQuery or(List<DBStoreQuery> queries) {
		List<DBStoreQuery> qp = new ArrayList<>(getNested());
		qp.add(new BasicQuery(queries, OPERATOR.OR));
		return new BasicQuery(qp, OPERATOR.AND);
	}

	public DBStoreQuery or(DBStoreQuery... queries) {
		List<DBStoreQuery> task = new ArrayList<>();
		for (DBStoreQuery q : queries) {
			task.add(q);
		}
		return or(task);
	}

	public DBStoreQuery order(String order, boolean asc) {
		List<OrderBy> ne = new ArrayList<>(getOrderBy());
		ne.add(new OrderBy(order, asc));
		return new BasicQuery(getNested(), operator, key, comparator, value, ne, start, max);
	}

	public DBStoreQuery order(String order) {
		return order(order, true);
	}

	public DBStoreQuery start(int start) {
		return new BasicQuery(getNested(), operator, key, comparator, value, getOrderBy(), start, max);
	}

	public DBStoreQuery max(int max) {
		return new BasicQuery(getNested(), operator, key, comparator, value, getOrderBy(), start, max);
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

	public DBStoreQuery elemMatch(String key, DBStoreQuery value) {
		return add(key, COMPARATOR.ELEM_MATCH, value);
	}

	public String toString() {
		StringBuffer out = new StringBuffer();
		if (key != null) {
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
				case ELEM_MATCH:
					out.append("==");
					break;
				default:
					break;
			}
			out.append(' ');
			out.append(value);
		} else {
			List<DBStoreQuery> n = getNested();
			out.append('(');
			for (DBStoreQuery fq : n) {
				if (n.indexOf(fq) > 0) {
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
		if (getOrderBy() != null && getOrderBy().size() > 0) {
			out.append(" ORDER BY ");
			for (OrderBy ob : getOrderBy()) {
				out.append('(');
				out.append(ob.getField());
				out.append(',');
				out.append(ob.isAsc() ? "ASC" : "DESC");
				out.append(')');
			}
		}
		if (start != 0 || max != -1) {
			out.append(" LIMIT (" + start + "," + max + ")");
		}
		return out.toString();
	}

}
