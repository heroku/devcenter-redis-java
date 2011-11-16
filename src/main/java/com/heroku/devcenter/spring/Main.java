package com.heroku.devcenter.spring;

import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

public class Main {

    
	public static void main(String[] args) {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
		RedisConfig config = ctx.getBean(RedisConfig.class);
        //Create the connection pool from the values in the URL
        //This pool can be stored in a singleton and reused
		JedisPool pool = new JedisPool(new Config(), config.getHost(), config.getPort(), Protocol.DEFAULT_TIMEOUT, config.getPassword());
		Jedis jedis = pool.getResource();
		String testValue = "testValueSpring";
		
		jedis.set("testKey", testValue);
		System.out.println("Value set into Redis is: " + testValue);
		
		System.out.println("Value retrieved from Redis is: " + jedis.get("testKey"));

	}

}
