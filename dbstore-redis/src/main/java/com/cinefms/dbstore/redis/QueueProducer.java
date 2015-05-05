package com.cinefms.dbstore.redis;

import javax.annotation.PostConstruct;

import org.redisson.Redisson;
import org.redisson.RedissonQueue;
import org.springframework.beans.factory.annotation.Autowired;

public class QueueProducer<T> {

	@Autowired
	private Redisson redisson;
	
	private RedissonQueue<T> queue;
	
	private String queueName;

	@PostConstruct
	public void init() {
		this.queue = (RedissonQueue<T>)redisson.getQueue(getQueueName());
	}
	
	
	public void send(T t) {
		try {
			queue.add(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getQueueName() {
		return queueName;
	}



	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	
	
	
}
