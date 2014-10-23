package com.cinefms.dbstore.api.impl;

import com.cinefms.dbstore.api.DBStoreEntity;
import com.cinefms.dbstore.api.DBStoreListener;

public abstract class DBStoreListenerAdapter implements DBStoreListener {

	public void beforeSave(String db, DBStoreEntity o) {
	}

	public void saved(String db, DBStoreEntity o) {
	}

	public void beforeDelete(String db, DBStoreEntity o) {
	}

	public void deleted(String db, DBStoreEntity o) {
	}

}
