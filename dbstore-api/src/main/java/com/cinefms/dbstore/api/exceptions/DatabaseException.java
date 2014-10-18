package com.cinefms.dbstore.api.exceptions;

public class DatabaseException extends Exception {

	private static final long serialVersionUID = 7641277880117124618L;

	public DatabaseException(Exception e) {
		super(e);
	}

}
