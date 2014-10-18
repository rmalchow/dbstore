package com.cinefms.dbstore.utils.mongo;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.skjlls.utils.db.SkjllsOrderBy;
import com.skjlls.utils.db.SkjllsQuery;
import com.skjlls.utils.db.SkjllsQuery.OPERATOR;


public class SkjllsQueryMongojackTranslator {
	
	private static Log log = LogFactory.getLog(SkjllsQueryMongojackTranslator.class);
	
	public Query translate(SkjllsQuery in) {
		Query q = DBQuery.empty();
		if(in.getField()!=null) {
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
			case CONTAINS:
				try {
					q = q.regex(in.getField(), Pattern.compile((String)in.getValue(),Pattern.CASE_INSENSITIVE));
				} catch (Exception e) {
					log.warn("broken regex....");
					String x = ((String)in.getValue()).replaceAll("[^\\w\\s]", "");
					q = q.regex(in.getField(), Pattern.compile(x,Pattern.CASE_INSENSITIVE));
				}
				break;
			case IN:
				log.debug(" ##### "+in.getField()+" --- "+in.getValue().getClass());
				q = q.in(in.getField(), (Collection<?>)in.getValue());
				break;
			default:
				break;
			}
		} else {
			List<SkjllsQuery> n = in.getNested();
			Query[] mq = new Query[n.size()];
			for(SkjllsQuery fq : n) {
				mq[n.indexOf(fq)] = translate(fq);
			}
			if(in.getOperator()==OPERATOR.AND) {
				q = q.and(mq);
			}
			if(in.getOperator()==OPERATOR.OR) {
				q = q.or(mq);
			}

		}
		return q;
	}

	public DBObject translateOrderBy(SkjllsQuery query) {
		if(query.getOrderBy()==null) {
			return null;
		}
		BasicDBObject out = new BasicDBObject();
		for(SkjllsOrderBy ob : query.getOrderBy()) {
			out.append(ob.getField(), ob.isAsc()?1:-1);
		}
		return out;
	}
	

}
