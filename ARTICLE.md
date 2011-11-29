## Using Redis from Java

There are several Java libraries available for connecting to Redis, including [Jedis](https://github.com/xetorthio/jedis) and [JRedis](http://code.google.com/p/jredis/). This guide will shows to use Jedis from both a generic Java application and a Spring configured application.

### Add Jedis to Dependencies

Include the Jedis library in your application by adding the following dependency to `pom.xml`:

    <dependency>
        <groupId>redis.clients</groupId>
        <artifactId>jedis</artifactId>
        <version>2.0.0</version>
    </dependency>

### Use Redis in your application

The connection information for the Redis Service provisioned by Redis To Go is stored as a URL in the `REDISTOGO_URL` config var. You can create a Jedis connection pool from this URL string with the following code snippet:

    :::java
    try {
    	URI redisURI = new URI(System.getenv("REDISTOGO_URL"));
    	JedisPool pool = new JedisPool(new JedisPoolConfig(),
    			redisURI.getHost(),
    			redisURI.getPort(),
    			Protocol.DEFAULT_TIMEOUT,
    			redisURI.getUserInfo().split(":",2)[1]);
    } catch (URISyntaxException e) {
        // URI couldn't be parsed. Handle exception
    }

Now you can use this pool to perform Redis operations. For example:

    :::java
    Jedis jedis = pool.getResource();
    try {
      /// ... do stuff here ... for example
      jedis.set("foo", "bar");
      String foobar = jedis.get("foo");
      jedis.zadd("sose", 0, "car"); jedis.zadd("sose", 0, "bike"); 
      Set<String> sose = jedis.zrange("sose", 0, -1);
    } finally {
      /// ... it's important to return the Jedis instance to the pool once you've finished using it
      pool.returnResource(jedis);
    }

(example taken directly from Jedis docs).

### Using Redis with Spring

Use the following Java Configuration class to set up a `JedisPool` instance as a singleton Spring bean:

    :::java
    @Configuration
    public class SpringConfig {

    	@Bean
    	public JedisPool getJedisPool() {
    		try {
    			URI redisURI = new URI(System.getenv("REDISTOGO_URL"));
    			return new JedisPool(new JedisPoolConfig(),
    					redisURI.getHost(),
    					redisURI.getPort(),
    					Protocol.DEFAULT_TIMEOUT,
    					redisURI.getUserInfo().split(":",2)[1]);
    		} catch (URISyntaxException e) {
    			throw new RuntimeException("Redis couldn't be configured from URL in REDISTOGO_URL env var:"+ 
    			                            System.getenv("REDISTOGO_URL"));
    		}
    	}
	
    }

or the following XML configuration file:

    :::xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
            http://www.springframework.org/schema/context
            http://www.springframework.org/schema/context/spring-context-3.0.xsd">

    <context:annotation-config/>
    <context:property-placeholder/>

    <bean id="jedisURI" class="java.net.URI">
        <constructor-arg value="${REDISTOGO_URL}"/>
    </bean>
    <bean id="jedisPoolConfig" class="redis.clients.jedis.JedisPoolConfig"/>
    <bean id="jedisPool" class="redis.clients.jedis.JedisPool">
        <constructor-arg index="0" ref="jedisPoolConfig"/>
        <constructor-arg index="1" value="#{ @jedisURI.getHost() }"/>
        <constructor-arg index="2" value="#{ @jedisURI.getPort() }"/>
        <constructor-arg index="3" value="#{ T(redis.clients.jedis.Protocol).DEFAULT_TIMEOUT }"/>
        <constructor-arg index="4" value="#{ @jedisURI.getUserInfo().split(':',2)[1] }"/>
    </bean>
    </beans>

### Sample code

To see a complete, working example, check out the [sample code in github](https://github.com/heroku/devcenter-redis-java). The [readme](https://github.com/heroku/devcenter-redis-java/blob/master/README.md) explains more about the example.