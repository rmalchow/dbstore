package com.skjlls.utils.redis;

import com.cinefms.dbstore.redis.QueueProducer;

public class TestProducer extends QueueProducer<String> {



	public void send() {
		for(int i=0;i<10000;i++) {
			this.send(i+"");
		}
	}
	
	
}
