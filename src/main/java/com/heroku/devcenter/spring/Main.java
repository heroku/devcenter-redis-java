package com.heroku.devcenter.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Main {

	public static void main(String[] args) {
		System.out.println("Launching Redis sample. Configured with Spring");
		ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
		//ApplicationContext ctx = new GenericXmlApplicationContext("applicationContext.xml");

		JedisPool pool = ctx.getBean(JedisPool.class);
		Jedis jedis = pool.getResource();
		try {
			String testValue = "testValueSpring";

			jedis.set("testKeySpring", testValue);
			System.out.println("Value set into Redis is: " + testValue);

			System.out.println("Value retrieved from Redis is: " + jedis.get("testKeySpring"));
		} finally {
			pool.returnResource(jedis);
		}
	}

}
