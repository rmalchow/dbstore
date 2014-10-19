package com.cinefms.dbstore.api.exceptions;

public class DBStoreException extends Exception {

	private static final long serialVersionUID = 7641277880117124618L;

	public DBStoreException(Exception e) {
		super(e);
	}

	public DBStoreException(String code, Exception e) {
		super(null,e);
	}
	
}
