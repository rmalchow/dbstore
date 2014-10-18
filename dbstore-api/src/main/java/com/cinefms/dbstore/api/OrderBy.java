package com.cinefms.dbstore.api;

public class OrderBy {
	
	private boolean asc = true;
	private String field;

	public OrderBy(String field, boolean asc) {
		super();
		this.asc = asc;
		this.field = field;
	}

	public OrderBy(String field) {
		this(field,true);
	}

	public OrderBy() {
		this(null,true);
	}
	
	public boolean isAsc() {
		return asc;
	}

	public void setAsc(boolean asc) {
		this.asc = asc;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

}
