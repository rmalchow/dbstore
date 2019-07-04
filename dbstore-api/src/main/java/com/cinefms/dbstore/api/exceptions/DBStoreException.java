package com.cinefms.dbstore.api.exceptions;

public class DBStoreException extends RuntimeException {

	private static final long serialVersionUID = 7641277880117124618L;

	public DBStoreException() {
		super();
	}

	public DBStoreException(String message) {
		super(message);
	}

	public DBStoreException(Exception ex) {
		super(ex);
	}

	public DBStoreException(String message, Exception ex) {
		super(message, ex);
	}


}
