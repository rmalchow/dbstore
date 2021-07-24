package com.cinefms.dbstore.utils.mongo.entities;

import com.cinefms.dbstore.api.annotations.CollectionName;
import com.cinefms.dbstore.api.annotations.Index;
import com.cinefms.dbstore.api.annotations.Indexes;
import com.cinefms.dbstore.api.impl.BaseDBStoreEntity;

import java.beans.ConstructorProperties;

@Indexes({
		@Index(name = "firstNameIdx", fields = "firstName"),
		@Index(name = "lastNameIdx", fields = "lastName"),
		@Index(name = "fullNameIdx", fields = {"firstName", "lastName"}, unique = true)
})
@CollectionName(CollectionName.USE_CLASS_NAME)
public class EntityWithIndexes extends BaseDBStoreEntity {
	private final String firstName;
	private final String lastName;

	@ConstructorProperties({"firstName", "lastName"})
	public EntityWithIndexes(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

}
