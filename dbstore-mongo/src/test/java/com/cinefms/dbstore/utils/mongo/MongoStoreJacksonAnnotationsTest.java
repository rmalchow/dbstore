package com.cinefms.dbstore.utils.mongo;

import com.cinefms.dbstore.utils.mongo.entities.Address;
import com.cinefms.dbstore.utils.mongo.entities.UserEntity;
import com.mongodb.DBObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MongoStoreJacksonAnnotationsTest extends MongoDataStoreTest {

	@Test
	public void itShouldFollowJacksonAnnotationsWhenSerializingOrDeserializingRecords() {
		Address firstAddress = new Address();
		firstAddress.setStreet("Armstrong st.");
		firstAddress.setBuildingNo(123);
		firstAddress.setCity("New York");
		firstAddress.setZipCode(1234567890L);

		Address secondAddress = new Address();
		secondAddress.setStreet("Langley St");
		secondAddress.setBuildingNo(13);
		secondAddress.setCity("London");
		secondAddress.setZipCode(9876543210L);

		UserEntity user = new UserEntity();
		user.setBirthday(new Date(2020, 5, 12));
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setPassword("secret-password");
		user.setAddresses(Arrays.asList(firstAddress, secondAddress));

		user = mds.saveObject("testdb", user);

		// Assert that user was returned back and it has set ID
		Assert.assertNotNull(user);
		Assert.assertNotNull(user.getId());

		// Check stored data
		DBObject record = mds.getDB("testdb")
				.getCollection(UserEntity.class.getName())
				.find()
				.next();

		Assert.assertNotNull(record);
		Assert.assertEquals(user.getId(), record.get("_id"));
		Assert.assertEquals(user.getUuid(), record.get("uuid"));
		Assert.assertEquals(new Date(2020, 5, 12), record.get("birthday"));
		Assert.assertEquals("John", record.get("firstName"));
		Assert.assertEquals("Doe", record.get("lastName"));
		Assert.assertNull(record.get("fullName"));    // getter is marked as ignored using Jackson annotation
		Assert.assertEquals(
				user.getEncryptedPassword(),
				record.get("password")
		);    // property is renamed using Jackson annotation
		Assert.assertNull(record.get("encryptedPassword"));

		Object addressesObject = record.get("addresses");
		Assert.assertNotNull(addressesObject);
		Assert.assertTrue(addressesObject instanceof List);

		List<DBObject> addresses = (List<DBObject>) addressesObject;
		Assert.assertEquals(2, addresses.size());

		DBObject firstAddressRecord = addresses.get(0);
		Assert.assertEquals("Armstrong st.", firstAddressRecord.get("street"));
		Assert.assertEquals(123, firstAddressRecord.get("buildingNo"));
		Assert.assertEquals("New York", firstAddressRecord.get("city"));
		Assert.assertEquals(1234567890L, firstAddressRecord.get("zipCode"));
		Assert.assertEquals("Armstrong st. 123, New York 1234567890", firstAddressRecord.get("fullName")); // hidden property
	}

}
