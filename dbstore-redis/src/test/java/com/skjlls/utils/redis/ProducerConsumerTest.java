package com.skjlls.utils.redis;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ProducerConsumerTest {

	public static void main(String[] args) {
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext("config_messaging.xml");
			while(true) {
				Thread.currentThread().sleep(2000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
