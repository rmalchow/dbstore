package com.cinefms.dbstore.api;

import javax.persistence.Id;



public interface DBStoreEntity {

	@Id
	public String getId();

	public void setId(String id);
	
	public String createId();
	

}
