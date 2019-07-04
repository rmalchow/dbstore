package com.cinefms.dbstore.api;

import com.cinefms.dbstore.query.api.DBStoreQuery;
import com.cinefms.dbstore.query.api.DBStoreQuery.COMPARATOR;
import com.cinefms.dbstore.query.api.DBStoreQuery.OPERATOR;
import com.cinefms.dbstore.query.api.exceptions.MalformedQueryException;
import com.cinefms.dbstore.query.api.impl.BasicQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

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


}
