package com.cinefms.dbstore.query.mongo;

import com.cinefms.dbstore.query.api.DBStoreQuery;
import com.cinefms.dbstore.query.api.DBStoreQuery.OPERATOR;
import com.cinefms.dbstore.query.api.impl.OrderBy;
import com.mongodb.client.model.Sorts;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.conversions.Bson;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// TODO: Rewrite to use com.mongodb.client.model.Filters instead of deprecated org.mongojack.DBQuery.Query
public class QueryMongojackTranslator {

	private static final Log LOGGER = LogFactory.getLog(QueryMongojackTranslator.class);

	public Query translate(DBStoreQuery in) {
		Query q = DBQuery.empty();
		if (in.getField() != null) {
			switch (in.getComparator()) {
				case EQ:
					q = q.is(in.getField(), in.getValue());
					break;
				case LTE:
					q = q.lessThanEquals(in.getField(), in.getValue());
					break;
				case LT:
					q = q.lessThan(in.getField(), in.getValue());
					break;
				case GTE:
					q = q.greaterThanEquals(in.getField(), in.getValue());
					break;
				case GT:
					q = q.greaterThan(in.getField(), in.getValue());
					break;
				case NE:
					q = q.notEquals(in.getField(), in.getValue());
					break;
				case ELEM_MATCH:
					q = q.elemMatch(in.getField(), translate((DBStoreQuery) in.getValue()));
					break;
				case CONTAINS:
					try {
						q = q.regex(in.getField(), Pattern.compile((String) in.getValue(), Pattern.CASE_INSENSITIVE));
					} catch (Exception ex) {
						LOGGER.warn("broken regex '" + in.getValue() + "' ....", ex);
						String x = ((String) in.getValue()).replaceAll("[^\\w\\s]", "");
						q = q.regex(in.getField(), Pattern.compile(x, Pattern.CASE_INSENSITIVE));
					}
					break;
				case IN:
					LOGGER.debug(" ##### " + in.getField() + " --- " + in.getValue().getClass());
					q = q.in(in.getField(), (Collection<?>) in.getValue());
					break;
				case NIN:
					LOGGER.debug(" ##### " + in.getField() + " --- " + in.getValue().getClass());
					Collection values = in.getValue() != null && in.getValue() instanceof Collection ? (List) in.getValue() : null;
					if (values != null && !values.isEmpty()) {
						q = q.notIn(in.getField(), values);
					} else {
						q = q.notExists(in.getField());
					}
					break;
				case EXISTS:
					q = q.exists(in.getField());
					break;
				case ALL:
					q = q.all(in.getField(), (Collection<?>) in.getValue());
					break;
				default:
					break;
			}
		} else {
			List<DBStoreQuery> n = in.getNested();
			if (n != null && !n.isEmpty()) {
				Query[] mq = new Query[n.size()];
				for (DBStoreQuery fq : n) {
					mq[n.indexOf(fq)] = translate(fq);
				}
				if (in.getOperator() == OPERATOR.AND) {
					q = q.and(mq);
				}
				if (in.getOperator() == OPERATOR.OR) {
					q = q.or(mq);
				}
			}
		}
		return q;
	}

	public Bson translateOrderBy(DBStoreQuery query) {
		List<OrderBy> orderBy = query.getOrderBy();

		if (orderBy == null || orderBy.isEmpty()) {
			return null;
		}

		return Sorts.orderBy(
				orderBy.stream()
						.map(it -> it.isAsc() ? Sorts.ascending(it.getField()) : Sorts.descending(it.getField()))
						.collect(Collectors.toList())
		);
	}

}
