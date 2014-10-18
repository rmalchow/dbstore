package com.cinefms.dbstore.api.impl;

import com.cinefms.dbstore.api.DBStoreEntity;
import com.cinefms.dbstore.api.DBStoreListener;

public abstract class DBStoreListenerAdapter implements DBStoreListener {

	public void beforeSave(DBStoreEntity o) {
	}

	public void saved(DBStoreEntity o) {
	}

	public void beforeDelete(DBStoreEntity o) {
	}

	public void deleted(DBStoreEntity o) {
	}

}
