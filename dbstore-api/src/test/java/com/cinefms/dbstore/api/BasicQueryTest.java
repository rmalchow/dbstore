package com.cinefms.dbstore.api;

import static com.cinefms.dbstore.api.impl.BasicQuery.createQuery;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.cinefms.dbstore.api.DBStoreQuery.COMPARATOR;
import com.cinefms.dbstore.api.DBStoreQuery.OPERATOR;
import com.cinefms.dbstore.api.exceptions.MalformedQueryException;
import com.cinefms.dbstore.api.impl.BasicQuery;

@RunWith(MockitoJUnitRunner.class)
public class BasicQueryTest {

	@Test
	public void testSimpleQuery() throws MalformedQueryException {
		DBStoreQuery DBStoreQuery = BasicQuery.createQuery().eq("A", "B");
		Assert.assertEquals("(A == B)", DBStoreQuery.toString());
		Assert.assertEquals(1, DBStoreQuery.getNested().size());
		Assert.assertEquals(COMPARATOR.EQ, DBStoreQuery.getNested().get(0).getComparator());
	}
	
	@Test
	public void testSimpleAndQuery() throws MalformedQueryException {
		DBStoreQuery DBStoreQuery = BasicQuery.createQuery().eq("A", "B").eq("X", "Z");
		Assert.assertEquals("(A == B AND X == Z)", DBStoreQuery.toString());
		Assert.assertEquals(OPERATOR.AND,DBStoreQuery.getOperator());
	}

	@Test
	public void testEmptyQuery() {
		DBStoreQuery DBStoreQuery = BasicQuery.createQuery();
		Assert.assertEquals("()", DBStoreQuery.toString());
	}

	
	@Test
	public void testNestedQuery() throws MalformedQueryException {
		DBStoreQuery DBStoreQuery = BasicQuery.createQuery().
				eq("A", "CCC").
				or(createQuery().eq("C", "XXX"),createQuery().eq("A", "XXX"));
		
		Assert.assertEquals("(A == CCC AND ((C == XXX) OR (A == XXX)))", DBStoreQuery.toString());
	}

	@Test
	public void testIllegalKeyQuery() throws MalformedQueryException {
		MalformedQueryException e=null;
		try {
			createQuery().eq(null,"C");
		} catch (MalformedQueryException mqe) {
			e = mqe;
		}
		Assert.assertNotNull(e);
	}
	
	@Test
	public void testSimpleQueryNE() throws MalformedQueryException {
		DBStoreQuery DBStoreQuery = createQuery().ne("A", "B");
		Assert.assertEquals("(A != B)", DBStoreQuery.toString());
	}
	
	@Test
	public void testSimpleQueryLTE() throws MalformedQueryException {
		DBStoreQuery DBStoreQuery = createQuery().lte("A", "B");
		Assert.assertEquals("(A <= B)", DBStoreQuery.toString());
	}
	
	@Test
	public void testSimpleQueryLE() throws MalformedQueryException {
		DBStoreQuery DBStoreQuery = BasicQuery.createQuery().lt("A", "B");
		Assert.assertEquals("(A < B)", DBStoreQuery.toString());
	}
	
	@Test
	public void testSimpleQueryGTE() throws MalformedQueryException {
		DBStoreQuery DBStoreQuery = BasicQuery.createQuery().gte("A", "B");
		Assert.assertEquals("(A >= B)", DBStoreQuery.toString());
	}
	
	@Test
	public void testSimpleQueryGT() throws MalformedQueryException {
		DBStoreQuery DBStoreQuery = BasicQuery.createQuery().gt("A", "B");
		Assert.assertEquals("(A > B)", DBStoreQuery.toString());
	}

	@Test
	public void testSimpleQueryGTWithDatabase() throws MalformedQueryException {
		DBStoreQuery q = BasicQuery.createQuery("nase").gt("A", "B");
		System.err.println("---- : "+q.getDatabase()+" / "+q.hashCode());
		Assert.assertEquals("(A > B) @ nase", q.toString());
	}
	
	@Test(expected=MalformedQueryException.class)
	public void testSimpleQueryGTWithDatabaseError() throws MalformedQueryException {
		DBStoreQuery q = BasicQuery.createQuery("nase").gt("A", "B");
		q.and(BasicQuery.createQuery("nase2").gt("A", "B"));
	}
	
	@Test
	public void testSort() throws MalformedQueryException {
		DBStoreQuery DBStoreQuery = BasicQuery.createQuery().order("a").order("b");
		Assert.assertEquals(2, DBStoreQuery.getOrderBy().size());
	}
	
	
}
