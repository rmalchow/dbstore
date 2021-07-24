package com.cinefms.dbstore.utils.mongo;

import com.cinefms.dbstore.utils.mongo.entities.ClassNameEntity;
import com.cinefms.dbstore.utils.mongo.entities.EntityWithIndexes;
import com.cinefms.dbstore.utils.mongo.entities.SimpleClassNameEntity;
import com.cinefms.dbstore.utils.mongo.utils.AssertCollection;
import com.mongodb.DBObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class MongoStoreAnnotationsTest extends MongoDataStoreTest {

	@Test
	public void itShouldUseAsCollectionNameEntityClassSimpleName() {
		SimpleClassNameEntity entity = new SimpleClassNameEntity();
		mds.saveObject("testdb", entity);

		// Assert that entity was returned back and it has set ID
		Assert.assertNotNull(entity);
		Assert.assertNotNull(entity.getId());

		// Check stored data
		DBObject record = mds.getDB("testdb")
				.getCollection(SimpleClassNameEntity.class.getSimpleName())
				.find()
				.next();

		// Assert entity was saved to correct collection
		Assert.assertNotNull(record);
		Assert.assertEquals(entity.getId(), record.get("_id"));
	}

	@Test
	public void itShouldUseAsCollectionNameEntityClassName() {
		ClassNameEntity entity = new ClassNameEntity();
		mds.saveObject("testdb", entity);

		// Assert that entity was returned back and it has set ID
		Assert.assertNotNull(entity);
		Assert.assertNotNull(entity.getId());

		// Check stored data
		DBObject record = mds.getDB("testdb")
				.getCollection(ClassNameEntity.class.getName())
				.find()
				.next();

		// Assert entity was saved to correct collection
		Assert.assertNotNull(record);
		Assert.assertEquals(entity.getId(), record.get("_id"));
	}

	@Test
	public void itShouldDefineIndexesOnCollection() {
		EntityWithIndexes entity = new EntityWithIndexes("John", "Doe");
		mds.saveObject("testdb", entity);

		// Assert that entity was returned back and it has set ID
		Assert.assertNotNull(entity);
		Assert.assertNotNull(entity.getId());

		// Check collection indexes
		List<DBObject> indexes = mds.getDB("testdb")
				.getCollection(EntityWithIndexes.class.getSimpleName())
				.getIndexInfo();

		Assert.assertEquals(indexes.size(), 4);
		AssertCollection.assertContains(indexes, index -> {
			Assert.assertEquals("_id_", index.get("name"));
			Assert.assertEquals(1, ((DBObject) index.get("key")).get("_id"));
		});
		AssertCollection.assertContains(indexes, index -> {
			Assert.assertEquals("firstName_1", index.get("name"));
			Assert.assertNotEquals(true, index.get("unique"));
			Assert.assertEquals(1, ((DBObject) index.get("key")).get("firstName"));
		});
		AssertCollection.assertContains(indexes, index -> {
			Assert.assertEquals("lastName_1", index.get("name"));
			Assert.assertNotEquals(true, index.get("unique"));
			Assert.assertEquals(1, ((DBObject) index.get("key")).get("lastName"));
		});
		AssertCollection.assertContains(indexes, index -> {
			Assert.assertEquals("firstName_1_lastName_1", index.get("name"));
			Assert.assertEquals(true, index.get("unique"));
			Assert.assertEquals(1, ((DBObject) index.get("key")).get("firstName"));
			Assert.assertEquals(1, ((DBObject) index.get("key")).get("lastName"));
		});
	}

}
