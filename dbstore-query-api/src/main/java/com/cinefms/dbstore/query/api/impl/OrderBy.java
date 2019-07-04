package com.cinefms.dbstore.query.api.impl;

public class OrderBy {

	private boolean asc;
	private String field;

	public OrderBy(String field, boolean asc) {
		this.asc = asc;
		this.field = field;
	}

	public OrderBy(String field) {
		this(field, true);
	}

	public OrderBy() {
		this(null, true);
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

	public static OrderBy asc(String field) {
		return of(field,true);
	}

	public static OrderBy desc(String field) {
		return of(field,false);
	}

	public static OrderBy of(String field, boolean asc) {
		return new OrderBy(field,asc);
	}

}
