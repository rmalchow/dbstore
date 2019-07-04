package com.cinefms.dbstore.api;

import com.cinefms.dbstore.query.api.DBStoreQuery;
import com.cinefms.dbstore.query.api.DBStoreQuery.COMPARATOR;
import com.cinefms.dbstore.query.api.DBStoreQuery.OPERATOR;
import com.cinefms.dbstore.query.api.exceptions.MalformedQueryException;
import com.cinefms.dbstore.query.api.impl.BasicQuery;
import com.cinefms.dbstore.query.api.impl.OrderBy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static com.cinefms.dbstore.query.api.impl.BasicQuery.createQuery;

@RunWith(MockitoJUnitRunner.class)
public class BasicQueryTest {

	@Test
	public void testSimpleQuery() {
		DBStoreQuery DBStoreQuery = BasicQuery.createQuery().eq("A", "B");
		Assert.assertEquals("(A == B)", DBStoreQuery.toString());
		Assert.assertEquals(1, DBStoreQuery.getNested().size());
		Assert.assertEquals(COMPARATOR.EQ, DBStoreQuery.getNested().get(0).getComparator());
	}

	@Test
	public void testSimpleAndQuery() {
		DBStoreQuery DBStoreQuery = BasicQuery.createQuery().eq("A", "B").eq("X", "Z");
		Assert.assertEquals("(A == B AND X == Z)", DBStoreQuery.toString());
		Assert.assertEquals(OPERATOR.AND, DBStoreQuery.getOperator());
	}

	@Test
	public void testEmptyQuery() {
		DBStoreQuery DBStoreQuery = BasicQuery.createQuery();
		Assert.assertEquals("()", DBStoreQuery.toString());
	}

	@Test
	public void testNestedQuery() {
		DBStoreQuery DBStoreQuery = BasicQuery.createQuery().
				eq("A", "CCC").
				or(createQuery().eq("C", "XXX"), createQuery().eq("A", "XXX"));

		Assert.assertEquals("(A == CCC AND ((C == XXX) OR (A == XXX)))", DBStoreQuery.toString());
	}

	@Test
	public void testIllegalKeyQuery() {
		MalformedQueryException e = null;
		try {
			createQuery().eq(null, "C");
		} catch (MalformedQueryException mqe) {
			e = mqe;
		}
		Assert.assertNotNull(e);
	}

	@Test
	public void testSimpleQueryIN1() {
		DBStoreQuery DBStoreQuery = createQuery().in("A", "B");
		Assert.assertEquals("(A IN [B])", DBStoreQuery.toString());
	}

	@Test
	public void testSimpleQueryIN2() {
		DBStoreQuery DBStoreQuery = createQuery().in("A", "B", "C", "D");
		Assert.assertEquals("(A IN [B, C, D])", DBStoreQuery.toString());
	}

	@Test
	public void testSimpleQueryIN3() {
		DBStoreQuery DBStoreQuery = createQuery().in("A", Arrays.asList("B", "C", "D"));
		Assert.assertEquals("(A IN [B, C, D])", DBStoreQuery.toString());
	}

	@Test
	public void testSimpleQueryNIN1() {
		DBStoreQuery DBStoreQuery = createQuery().nin("A", "B");
		Assert.assertEquals("(A NIN [B])", DBStoreQuery.toString());
	}

	@Test
	public void testSimpleQueryNIN2() {
		DBStoreQuery DBStoreQuery = createQuery().nin("A", "B", "C", "D");
		Assert.assertEquals("(A NIN [B, C, D])", DBStoreQuery.toString());
	}

	@Test
	public void testSimpleQueryNIN3() {
		DBStoreQuery DBStoreQuery = createQuery().nin("A", Arrays.asList("B", "C", "D"));
		Assert.assertEquals("(A NIN [B, C, D])", DBStoreQuery.toString());
	}

	@Test
	public void testSimpleQueryNIN4() {
		DBStoreQuery DBStoreQuery = createQuery().nin("A");
		Assert.assertEquals("(A NIN)", DBStoreQuery.toString());
	}

	@Test
	public void testSimpleQueryALL1() {
		DBStoreQuery DBStoreQuery = createQuery().all("A", "B");
		Assert.assertEquals("(A ALL [B])", DBStoreQuery.toString());
	}

	@Test
	public void testSimpleQueryALL2() {
		DBStoreQuery DBStoreQuery = createQuery().all("A", "B", "C", "D");
		Assert.assertEquals("(A ALL [B, C, D])", DBStoreQuery.toString());
	}

	@Test
	public void testSimpleQueryALL3() {
		DBStoreQuery DBStoreQuery = createQuery().all("A", Arrays.asList("B", "C", "D"));
		Assert.assertEquals("(A ALL [B, C, D])", DBStoreQuery.toString());
	}

	@Test
	public void testSimpleQueryEXISTS() {
		DBStoreQuery DBStoreQuery = createQuery().exists("A");
		Assert.assertEquals("(A EXISTS)", DBStoreQuery.toString());
	}

	@Test
	public void testSimpleQueryNE() {
		DBStoreQuery DBStoreQuery = createQuery().ne("A", "B");
		Assert.assertEquals("(A != B)", DBStoreQuery.toString());
	}

	@Test
	public void testSimpleQueryLTE() {
		DBStoreQuery DBStoreQuery = createQuery().lte("A", "B");
		Assert.assertEquals("(A <= B)", DBStoreQuery.toString());
	}

	@Test
	public void testSimpleQueryLE() {
		DBStoreQuery DBStoreQuery = BasicQuery.createQuery().lt("A", "B");
		Assert.assertEquals("(A < B)", DBStoreQuery.toString());
	}

	@Test
	public void testSimpleQueryGTE() {
		DBStoreQuery DBStoreQuery = BasicQuery.createQuery().gte("A", "B");
		Assert.assertEquals("(A >= B)", DBStoreQuery.toString());
	}

	@Test
	public void testSimpleQueryGT() {
		DBStoreQuery DBStoreQuery = BasicQuery.createQuery().gt("A", "B");
		Assert.assertEquals("(A > B)", DBStoreQuery.toString());
	}

	@Test
	public void testElemMatchDatabase() {
		DBStoreQuery q1 = BasicQuery.createQuery().eq("A", "B");
		DBStoreQuery q2 = BasicQuery.createQuery().eq("X", "Z");
		q1 = q1.elemMatch("M", q2);

		Assert.assertEquals("(A == B AND M == (X == Z))", q1.toString());
	}

	@Test
	public void testOrderBy1() {
		DBStoreQuery q = BasicQuery.createQuery().eq("A", "B")
				.order("A")
				.order("B", true)
				.order("C", false);

		List<OrderBy> orders = q.getOrderBy();
		Assert.assertEquals(3, orders.size());
		Assert.assertEquals("A", orders.get(0).getField());
		Assert.assertEquals(true, orders.get(0).isAsc());
		Assert.assertEquals("B", orders.get(1).getField());
		Assert.assertEquals(true, orders.get(1).isAsc());
		Assert.assertEquals("C", orders.get(2).getField());
		Assert.assertEquals(false, orders.get(2).isAsc());
	}

	@Test
	public void testOrderBy2() {
		DBStoreQuery q = BasicQuery.createQuery().eq("A", "B")
				.order("A")
				.order("B", true)
				.order("C", false)
				.order(OrderBy.asc("D"), OrderBy.desc("E"));

		List<OrderBy> orders = q.getOrderBy();
		Assert.assertEquals(5, orders.size());
		Assert.assertEquals("A", orders.get(0).getField());
		Assert.assertEquals(true, orders.get(0).isAsc());
		Assert.assertEquals("B", orders.get(1).getField());
		Assert.assertEquals(true, orders.get(1).isAsc());
		Assert.assertEquals("C", orders.get(2).getField());
		Assert.assertEquals(false, orders.get(2).isAsc());
		Assert.assertEquals("D", orders.get(3).getField());
		Assert.assertEquals(true, orders.get(3).isAsc());
		Assert.assertEquals("E", orders.get(4).getField());
		Assert.assertEquals(false, orders.get(4).isAsc());
	}

	@Test
	public void testOrderBy3() {
		DBStoreQuery q = BasicQuery.createQuery().eq("A", "B")
				.order("A")
				.eq("D", 123)
				.and()
				.or();

		List<OrderBy> orders = q.getOrderBy();
		Assert.assertEquals(1, orders.size());
		Assert.assertEquals("A", orders.get(0).getField());
		Assert.assertEquals(true, orders.get(0).isAsc());
	}

	@Test
	public void testStartMax1() {
		DBStoreQuery q = BasicQuery.createQuery().eq("A", "B")
				.start(10)
				.max(20);

		Assert.assertEquals(10, q.getStart());
		Assert.assertEquals(20, q.getMax());
	}

	@Test
	public void testStartMax2() {
		DBStoreQuery q = BasicQuery.createQuery().eq("A", "B")
				.start(10)
				.max(20)
				.eq("D", 123)
				.and()
				.or();

		Assert.assertEquals(10, q.getStart());
		Assert.assertEquals(20, q.getMax());
	}

}
