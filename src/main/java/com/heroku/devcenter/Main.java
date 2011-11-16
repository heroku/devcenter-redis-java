package com.heroku.devcenter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.pool.impl.GenericObjectPool.Config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

public class Main {

    protected static final Pattern HEROKU_REDISTOGO_URL_PATTERN = Pattern.compile("^redis://([^:]*):([^@]*)@([^:]*):([^/]*)(/)?");
    
	public static void main(String[] args) {
		//Parse the configuration URL
        Matcher matcher = HEROKU_REDISTOGO_URL_PATTERN.matcher(System.getenv("REDISTOGO_URL"));
        matcher.matches();		
		
        //Create the connection pool from the values in the URL
        //This pool can be stored in a singleton and reused
		JedisPool pool = new JedisPool(new Config(), matcher.group(3), Integer.parseInt(matcher.group(4)), Protocol.DEFAULT_TIMEOUT, matcher.group(2));
		Jedis jedis = pool.getResource();
		String testValue = "testValue";
		
		jedis.set("testKey", testValue);
		System.out.println("Value set into Redis is: " + testValue);
		
		System.out.println("Value retrieved from Redis is: " + jedis.get("testKey"));

	}

}
