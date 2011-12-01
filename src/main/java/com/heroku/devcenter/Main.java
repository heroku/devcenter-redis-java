package com.heroku.devcenter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.pool.impl.GenericObjectPool.Config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class Main {

	public static void main(String[] args) {
		System.out.println("Launching Redis sample.");
		JedisPool pool;
		try {
			URI redisURI = new URI(System.getenv("REDISTOGO_URL"));
			pool = new JedisPool(new JedisPoolConfig(),
					redisURI.getHost(), 
					redisURI.getPort(),
					Protocol.DEFAULT_TIMEOUT, 
					redisURI.getUserInfo().split(":",2)[1]);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Redis couldn't be configured from URL in REDISTOGO_URL env var ");
		}
		Jedis jedis = pool.getResource();
		try {
			String testValue = "testValue";
	
			jedis.set("testKey", testValue);
			System.out.println("Value set into Redis is: " + testValue);
	
			System.out.println("Value retrieved from Redis is: "
					+ jedis.get("testKey"));
		} finally {
			pool.returnResource(jedis);
		}
	}

}
