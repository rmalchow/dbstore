package com.cinefms.dbstore.api;

import javax.persistence.Id;


public interface DBStoreEntity {

	@Id
	String getId();

	void setId(String id);

	String createId();

}
