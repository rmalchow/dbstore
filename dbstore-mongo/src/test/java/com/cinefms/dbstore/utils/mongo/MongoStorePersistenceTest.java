package com.cinefms.dbstore.utils.mongo;

import com.cinefms.dbstore.query.api.impl.BasicQuery;
import com.cinefms.dbstore.utils.mongo.entities.SimpleEntity;
import com.cinefms.dbstore.utils.mongo.utils.AssertCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MongoStorePersistenceTest extends MongoDataStoreTest {

	@Test
	public void itShouldCreateRecord() {
		SimpleEntity entity = new SimpleEntity("test-value");

		SimpleEntity savedEntity = mds.saveObject(null, entity);

		Assert.assertNotNull(savedEntity);
		Assert.assertEquals(savedEntity.getId(), entity.getId());
		Assert.assertEquals(savedEntity.getValue(), entity.getValue());

		// Check stored data
		DBObject record = mds.getDB(null)
				.getCollection(SimpleEntity.class.getName())
				.find()
				.next();

		Assert.assertNotNull(record);
		Assert.assertEquals(entity.getId(), record.get("_id"));
		Assert.assertEquals(entity.getValue(), record.get("value"));
	}

	@Test
	public void itShouldUpdateExistingRecord() {
		SimpleEntity unchangedEntity = new SimpleEntity("test-value");
		mds.saveObject(null, unchangedEntity);

		SimpleEntity editedEntity = new SimpleEntity("test-value");
		mds.saveObject(null, editedEntity);

		editedEntity.setValue("new-value");
		SimpleEntity savedEntity = mds.saveObject(null, editedEntity);

		Assert.assertNotNull(savedEntity);
		Assert.assertEquals(savedEntity.getId(), editedEntity.getId());
		Assert.assertEquals(savedEntity.getValue(), editedEntity.getValue());

		List<DBObject> records = new ArrayList<>();
		mds.getDB(null)
				.getCollection(SimpleEntity.class.getName())
				.find()
				.forEach(records::add);

		Assert.assertEquals(2, records.size());
		AssertCollection.assertContains(records, record -> {
			Assert.assertEquals(unchangedEntity.getId(), record.get("_id"));
			Assert.assertEquals(unchangedEntity.getValue(), record.get("value"));
		});
		AssertCollection.assertContains(records, record -> {
			Assert.assertEquals(editedEntity.getId(), record.get("_id"));
			Assert.assertEquals(editedEntity.getValue(), record.get("value"));
		});
	}

	@Test
	public void itShouldUpdateMultipleExistingRecordsAtOnce() {
		SimpleEntity firstEntity = new SimpleEntity("first-entity");
		SimpleEntity secondEntity = new SimpleEntity("second-entity");
		List<SimpleEntity> savedEntities = mds.saveObjects(null, Arrays.asList(firstEntity, secondEntity));

		AssertCollection.assertContains(savedEntities, entity -> {
			Assert.assertEquals(firstEntity.getId(), entity.getId());
		});
		AssertCollection.assertContains(savedEntities, entity -> {
			Assert.assertEquals(secondEntity.getId(), entity.getId());
		});

		// Check stored data
		Assert.assertEquals(
				2,
				mds.getDB(null)
						.getCollection(SimpleEntity.class.getName())
						.find()
						.count()
		);

		// Update existing & insert new entities
		firstEntity.setValue("first-entity-updated");
		secondEntity.setValue("second-entity-updated");
		SimpleEntity thirdEntity = new SimpleEntity("third-entity");
		SimpleEntity fourthEntity = new SimpleEntity("fourth-entity");
		List<SimpleEntity> updatedEntities = mds.saveObjects(
				null,
				Arrays.asList(firstEntity, secondEntity, thirdEntity, fourthEntity)
		);

		AssertCollection.assertContains(updatedEntities, entity -> {
			Assert.assertEquals(firstEntity.getId(), entity.getId());
		});
		AssertCollection.assertContains(updatedEntities, entity -> {
			Assert.assertEquals(secondEntity.getId(), entity.getId());
		});
		AssertCollection.assertContains(updatedEntities, entity -> {
			Assert.assertEquals(thirdEntity.getId(), entity.getId());
		});
		AssertCollection.assertContains(updatedEntities, entity -> {
			Assert.assertEquals(firstEntity.getId(), entity.getId());
		});

		// Check stored data
		List<DBObject> records = new ArrayList<>();
		mds.getDB(null)
				.getCollection(SimpleEntity.class.getName())
				.find()
				.forEach(records::add);

		Assert.assertEquals(4, records.size());
		AssertCollection.assertContains(records, record -> {
			Assert.assertEquals(firstEntity.getId(), record.get("_id"));
			Assert.assertEquals("first-entity-updated", record.get("value"));
		});
		AssertCollection.assertContains(records, record -> {
			Assert.assertEquals(secondEntity.getId(), record.get("_id"));
			Assert.assertEquals("second-entity-updated", record.get("value"));
		});
		AssertCollection.assertContains(records, record -> {
			Assert.assertEquals(thirdEntity.getId(), record.get("_id"));
			Assert.assertEquals("third-entity", record.get("value"));
		});
		AssertCollection.assertContains(records, record -> {
			Assert.assertEquals(fourthEntity.getId(), record.get("_id"));
			Assert.assertEquals("fourth-entity", record.get("value"));
		});
	}

	@Test
	public void itShouldDeleteExistingEntity() {
		SimpleEntity entity = new SimpleEntity("test-value");
		mds.saveObject(null, entity);

		boolean result = mds.deleteObject(null, entity);
		Assert.assertTrue(result);

		// Check stored data
		DBCursor cursor = mds.getDB(null)
				.getCollection(SimpleEntity.class.getName())
				.find();

		Assert.assertEquals(0, cursor.count());
	}

	@Test
	public void itShouldReturnFalseIfDeletedEntityThatDoesNotExists() {
		SimpleEntity entity = new SimpleEntity("test-value");
		mds.saveObject(null, entity);

		mds.deleteObject(null, entity);
		boolean result = mds.deleteObject(null, entity);
		Assert.assertFalse(result);

		// Check stored data
		DBCursor cursor = mds.getDB(null)
				.getCollection(SimpleEntity.class.getName())
				.find();

		Assert.assertEquals(0, cursor.count());
	}

	@Test
	public void itShouldDeleteExistingEntityById() {
		SimpleEntity entity = new SimpleEntity("test-value");
		mds.saveObject(null, entity);

		boolean result = mds.deleteObject(null, SimpleEntity.class, entity.getId());
		Assert.assertTrue(result);

		// Check stored data
		DBCursor cursor = mds.getDB(null)
				.getCollection(SimpleEntity.class.getName())
				.find();

		Assert.assertEquals(0, cursor.count());
	}

	@Test
	public void itShouldReturnFalseIfDeletedEntityByIdDoesNotExists() {
		SimpleEntity entity = new SimpleEntity("test-value");
		mds.saveObject(null, entity);

		mds.deleteObject(null, SimpleEntity.class, entity.getId());
		boolean result = mds.deleteObject(null, SimpleEntity.class, entity.getId());
		Assert.assertFalse(result);

		// Check stored data
		DBCursor cursor = mds.getDB(null)
				.getCollection(SimpleEntity.class.getName())
				.find();

		Assert.assertEquals(0, cursor.count());
	}

	@Test
	public void itShouldDeleteMultipleEntitiesAtOnce() {
		SimpleEntity firstEntity = new SimpleEntity("first-entity");
		SimpleEntity secondEntity = new SimpleEntity("second-entity");
		SimpleEntity thirdEntity = new SimpleEntity("third-entity");
		SimpleEntity fourthEntity = new SimpleEntity("fourth-entity");
		mds.saveObjects(null, Arrays.asList(firstEntity, secondEntity, thirdEntity, fourthEntity));

		mds.deleteObjects(
				null,
				SimpleEntity.class,
				BasicQuery.createQuery().in("_id", firstEntity.getId(), thirdEntity.getId())
		);

		// Check stored data
		List<DBObject> records = new ArrayList<>();
		mds.getDB(null)
				.getCollection(SimpleEntity.class.getName())
				.find()
				.forEach(records::add);

		Assert.assertEquals(2, records.size());
		AssertCollection.assertContains(records, record -> {
			Assert.assertEquals(secondEntity.getId(), record.get("_id"));
			Assert.assertEquals("second-entity", record.get("value"));
		});
		AssertCollection.assertContains(records, record -> {
			Assert.assertEquals(fourthEntity.getId(), record.get("_id"));
			Assert.assertEquals("fourth-entity", record.get("value"));
		});
	}

}
