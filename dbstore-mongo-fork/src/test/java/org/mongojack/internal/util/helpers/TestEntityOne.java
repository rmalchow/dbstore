package org.mongojack.internal.util.helpers;

import java.util.List;

import javax.persistence.Id;

public class TestEntityOne {

	@Id
	private String id;
	
	private List<String> strings;

	public List<String> getStrings() {
		return strings;
	}

	public void setStrings(List<String> strings) {
		this.strings = strings;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
