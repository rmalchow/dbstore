package com.cinefms.dbstore.cache.memcached;

public class CacheUpdate {

	private String namespace;
	private Long newGeneration;

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public Long getNewGeneration() {
		return newGeneration;
	}

	public void setNewGeneration(Long newGeneration) {
		this.newGeneration = newGeneration;
	}

}
