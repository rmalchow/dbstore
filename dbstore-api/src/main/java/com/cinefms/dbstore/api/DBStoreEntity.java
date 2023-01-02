package com.cinefms.dbstore.api;

import org.mongojack.Id;

public interface DBStoreEntity {

	@Id
	String getId();

	void setId(String id);

	String createId();

}
