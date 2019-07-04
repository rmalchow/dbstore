package com.cinefms.dbstore.query.api.impl;

import com.cinefms.dbstore.query.api.DBStoreQuery;
import com.cinefms.dbstore.query.api.exceptions.MalformedQueryException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class BasicQuery implements DBStoreQuery {

	private List<DBStoreQuery> conditions;
	private String key;
	private Object value;
	private OPERATOR operator;
	private COMPARATOR comparator;

	private List<OrderBy> orderBy;
	private int start = 0;
	private int max = -1;


	private BasicQuery() {
		conditions = new ArrayList<>();
		orderBy = new ArrayList<>();
		operator = OPERATOR.AND;
	}

	@Deprecated
	private BasicQuery(List<DBStoreQuery> conditions, OPERATOR operator) {
		this.conditions = new ArrayList<>(conditions);
		this.operator = operator;
		this.orderBy = Collections.emptyList();
	}

	private BasicQuery(String key, COMPARATOR comparator, Object value) {
		this.key = key;
		this.comparator = comparator;
		this.value = value;
	}

	private BasicQuery(List<DBStoreQuery> conditions, OPERATOR operator, List<OrderBy> orderBy, int start, int max) {
		this.conditions = new ArrayList<>(conditions);
		this.operator = operator;
		this.orderBy = new ArrayList<>(orderBy);
		this.start = start;
		this.max = max;
	}

	public static DBStoreQuery createQuery() {
		return new BasicQuery();
	}

	public OPERATOR getOperator() {
		return operator;
	}

	public List<DBStoreQuery> getNested() {
		return conditions != null ? Collections.unmodifiableList(conditions) : Collections.emptyList();
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

	private DBStoreQuery add(String key, COMPARATOR c, Object value) {
		if (key == null) throw new MalformedQueryException(MalformedQueryException.ERROR_CODE.KEY_MUST_NOT_BE_NULL);

		List<DBStoreQuery> nConditions = new ArrayList<>(conditions);
		nConditions.add(new BasicQuery(key, c, value));

		return new BasicQuery(nConditions, OPERATOR.AND, orderBy, start, max);
	}

	public DBStoreQuery in(String key, Object... values) {
		return in(key, Arrays.asList(values));
	}

	public DBStoreQuery in(String key, List<?> values) {
		return add(key, COMPARATOR.IN, values);
	}

	@Override
	public DBStoreQuery nin(String key, Object... values) {
		return nin(key, values != null ? Arrays.asList(values) : Collections.emptyList());
	}

	@Override
	public DBStoreQuery nin(String key, List<?> values) {
		return add(key, COMPARATOR.NIN, values != null ? values : Collections.emptyList());
	}

	@Override
	public DBStoreQuery exists(String key) {
		return add(key, COMPARATOR.EXISTS, Collections.emptyList());
	}

	public DBStoreQuery all(String key, Object... values) {
		return all(key, Arrays.asList(values));
	}

	public DBStoreQuery all(String key, List<?> values) {
		return add(key, COMPARATOR.ALL, values);
	}

	public DBStoreQuery contains(String key, String value) {
		return add(key, COMPARATOR.CONTAINS, value);
	}

	public DBStoreQuery eq(String key, Object value) {
		return add(key, COMPARATOR.EQ, value);
	}

	public DBStoreQuery lte(String key, Object value) {
		return add(key, COMPARATOR.LTE, value);
	}

	public DBStoreQuery lt(String key, Object value) {
		return add(key, COMPARATOR.LT, value);
	}

	public DBStoreQuery gte(String key, Object value) {
		return add(key, COMPARATOR.GTE, value);
	}

	public DBStoreQuery gt(String key, Object value) {
		return add(key, COMPARATOR.GT, value);
	}

	public DBStoreQuery ne(String key, Object value) {
		return add(key, COMPARATOR.NE, value);
	}

	public DBStoreQuery and(DBStoreQuery... queries) {
		return and(Arrays.asList(queries));
	}

	public DBStoreQuery and(List<DBStoreQuery> queries) {
		List<DBStoreQuery> qp = new ArrayList<>(conditions);
		qp.add(new BasicQuery(queries, OPERATOR.AND));
		return new BasicQuery(qp, OPERATOR.AND, orderBy, start, max);
	}

	public DBStoreQuery or(DBStoreQuery... queries) {
		return or(Arrays.asList(queries));
	}

	public DBStoreQuery or(List<DBStoreQuery> queries) {
		List<DBStoreQuery> qp = new ArrayList<>(conditions);
		qp.add(new BasicQuery(queries, OPERATOR.OR));
		return new BasicQuery(qp, OPERATOR.AND, orderBy, start, max);
	}

	public DBStoreQuery order(String order) {
		return order(order, true);
	}

	public DBStoreQuery order(String order, boolean asc) {
		List<OrderBy> nOrderBy = new ArrayList<>(orderBy);
		nOrderBy.add(new OrderBy(order, asc));
		return new BasicQuery(conditions, operator, nOrderBy, start, max);
	}

	public DBStoreQuery order(OrderBy... orders) {
		List<OrderBy> nOrderBy = new ArrayList<>(orderBy);
		nOrderBy.addAll(Arrays.asList(orders));
		return new BasicQuery(conditions, operator, nOrderBy, start, max);
	}

	public DBStoreQuery start(int start) {
		return new BasicQuery(conditions, operator, orderBy, start, max);
	}

	public DBStoreQuery max(int max) {
		return new BasicQuery(conditions, operator, orderBy, start, max);
	}

	public List<OrderBy> getOrderBy() {
		return orderBy != null ? Collections.unmodifiableList(orderBy) : Collections.emptyList();
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
			if (value instanceof List) {
				switch (comparator) {
					case IN:
						out.append("IN");
						break;
					case NIN:
						out.append("NIN");
						break;
					case EXISTS:
						out.append("EXISTS");
						break;
					case ALL:
						out.append("ALL");
						break;
				}
				switch (comparator) {
					case NIN:
					case EXISTS:
						if (!((List) value).isEmpty()) {
							out.append(' ');
							out.append(((List) value).stream().collect(Collectors.joining(", ", "[", "]")));
						}
						break;
					default:
						out.append(' ');
						out.append(((List) value).stream().collect(Collectors.joining(", ", "[", "]")));
						break;
				}
			} else {
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
			}
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
