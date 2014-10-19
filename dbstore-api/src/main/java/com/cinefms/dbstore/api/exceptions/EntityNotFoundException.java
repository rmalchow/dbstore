package com.cinefms.dbstore.api.exceptions;

public class EntityNotFoundException extends DBStoreException {

	private static final long serialVersionUID = -1726052637799309086L;
	private String object;
	private String id;

	public EntityNotFoundException(String object, String id) {
		super(null);
		this.object = object;
		this.id = id;
	}

	public EntityNotFoundException(Class<?> clazz, String id) {
		this(clazz.getCanonicalName(),id);
	}
	
	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
