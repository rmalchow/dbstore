package com.skjlls.utils.redis;

import com.cinefms.dbstore.redis.QueueConsumer;

public class TestConsumer extends QueueConsumer<String> {
	
	private String name;

	@Override
	public void receive(String t) {
		Integer i = Integer.parseInt(t);
		if(i%1000==0) {
			System.err.println(getName()+": " +t);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
