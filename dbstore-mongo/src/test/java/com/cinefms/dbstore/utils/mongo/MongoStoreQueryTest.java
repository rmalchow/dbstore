package com.cinefms.dbstore.utils.mongo;

import com.cinefms.dbstore.query.api.impl.BasicQuery;
import com.cinefms.dbstore.utils.mongo.entities.SimpleEntity;
import com.cinefms.dbstore.utils.mongo.utils.AssertCollection;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class MongoStoreQueryTest extends MongoDataStoreTest {

	SimpleEntity firstEntity;
	SimpleEntity secondEntity;
	SimpleEntity thirdEntity;
	SimpleEntity fourthEntity;

	@Override
	public void setup() {
		super.setup();

		firstEntity = new SimpleEntity("first-entity");
		secondEntity = new SimpleEntity("second-entity");
		thirdEntity = new SimpleEntity("third-entity");
		fourthEntity = new SimpleEntity("fourth-entity");
		mds.saveObjects(null, Arrays.asList(firstEntity, secondEntity, thirdEntity, fourthEntity));
	}

	@Test
	public void itShouldReturnEntityById() {
		SimpleEntity result = mds.getObject(null, SimpleEntity.class, secondEntity.getId());
		Assert.assertNotNull(result);
		Assert.assertEquals(secondEntity.getId(), result.getId());
		Assert.assertEquals(secondEntity.getValue(), result.getValue());
	}

	@Test
	public void itShouldReturnNullIfRequestedEntityByIdDoesNotExists() {
		SimpleEntity result = mds.getObject(null, SimpleEntity.class, "non-existing");
		Assert.assertNull(result);
	}

	@Test
	public void itShouldReturnAllMatchingEntities() {
		List<SimpleEntity> results = mds.findObjects(
				null,
				SimpleEntity.class,
				BasicQuery.createQuery().contains("value", "d-entity")
		);

		Assert.assertEquals(2, results.size());
		AssertCollection.assertContains(results, result -> {
			Assert.assertEquals(secondEntity.getId(), result.getId());
		});
		AssertCollection.assertContains(results, result -> {
			Assert.assertEquals(thirdEntity.getId(), result.getId());
		});
	}

	@Test
	public void itShouldReturnEmptyListForNoMatchingEntities() {
		List<SimpleEntity> results = mds.findObjects(
				null,
				SimpleEntity.class,
				BasicQuery.createQuery().contains("value", "not-matching")
		);

		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
	}

	@Test
	public void itShouldReturnNumberOfAllMatchingEntities() {
		int results = mds.countObjects(
				null,
				SimpleEntity.class,
				BasicQuery.createQuery().contains("value", "d-entity")
		);

		Assert.assertEquals(2, results);
	}

	@Test
	public void itShouldReturnZeroIfNoMatchingEntities() {
		int results = mds.countObjects(
				null,
				SimpleEntity.class,
				BasicQuery.createQuery().contains("value", "not-matching")
		);

		Assert.assertEquals(0, results);
	}

	@Test
	public void itShouldReturnMatchingEntity() {
		SimpleEntity result = mds.findObject(
				null,
				SimpleEntity.class,
				BasicQuery.createQuery().eq("_id", thirdEntity.getId())
		);

		Assert.assertNotNull(result);
		Assert.assertEquals(thirdEntity.getId(), result.getId());
	}

	@Test
	public void itShouldReturnNullIfMatchingEntity() {
		SimpleEntity result = mds.findObject(
				null,
				SimpleEntity.class,
				BasicQuery.createQuery().eq("_id", "non-existing-id")
		);

		Assert.assertNull(result);
	}

	@Test
	public void itShouldReturnAnyMatchingEntityIfMultipleOptions() {
		SimpleEntity result = mds.findObject(
				null,
				SimpleEntity.class,
				BasicQuery.createQuery().contains("value", "d-entity")
		);

		Assert.assertNotNull(result);
		Assert.assertTrue(
				Arrays.asList(secondEntity.getId(), thirdEntity.getId()).contains(result.getId())
		);
	}

}
