package com.cinefms.dbstore.redis;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.redisson.Redisson;
import org.redisson.RedissonQueue;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class QueueConsumer<T> {
	
	public static Log log = LogFactory.getLog(QueueConsumer.class);

	@Autowired
	private Redisson redisson;
	
	private RedissonQueue<T> queue;
	
	private String queueName;
	
	private int threads = 0;

	@PostConstruct
	public void init() {
		this.queue = (RBlockingQueue<T>)redisson.getQueue(getQueueName());
	}
	
	
	public abstract void receive(T t);
	

	public void poll() {
		do {
			T t = queue.
			if(t!=null) {
				try {
					receive(t);
				} catch (Exception e) {
					log.error("error polling from queue "+getQueueName()+": ",e);
				}
		    } else {
		    	break;
		    }
		} while(true);
	}
	
	
	public String getQueueName() {
		return queueName;
	}


	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	
	
}
