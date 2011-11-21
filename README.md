## Using Redis from Java

There are several Java libraries available for connecting to Redis, including [Jedis](https://github.com/xetorthio/jedis) and [JRedis](http://code.google.com/p/jredis/). This guide will show how to use Jedis from both a generic Java application and a Spring configured application.

### Add Jedis to Dependencies

Include the Jedis library in your application by adding the following dependency to `pom.xml`:

    <dependency>
        <groupId>redis.clients</groupId>
        <artifactId>jedis</artifactId>
        <version>2.0.0</version>
    </dependency>

### Use Redis in Your Application

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

Using the following Java Configuration class to set up a `JedisPool` instance as a singleton Spring bean:

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

### Sample Code

To see a complete, working example, check out the [sample code in github](https://github.com/heroku/devcenter-redis-java).

Clone the repo with:

    $ git clone https://github.com/heroku/devcenter-redis-java.git

Start up Redis locally and set the `REDISTOGO_URL` environment variable:

    $ export REDISTOGO_URL="redis://:@localhost:6379"

Build the sample:

    $ mvn package
    [INFO] Scanning for projects...
    [INFO]                                                                         
    [INFO] ------------------------------------------------------------------------
    [INFO] Building redisSample 0.0.1-SNAPSHOT
    [INFO] ------------------------------------------------------------------------
    ...

Run it with foreman:

    $ foreman start
    22:21:21 web.1     | started with pid 46300
    22:21:21 web.1     | Nov 20, 2011 10:21:21 PM org.springframework.context.support.AbstractApplicationContext prepareRefresh
    22:21:21 web.1     | INFO: Refreshing org.springframework.context.annotation.AnnotationConfigApplicationContext@182d9c06: startup date [Sun Nov 20 22:21:21 PST 2011]; root of context hierarchy
    22:21:21 web.1     | Nov 20, 2011 10:21:21 PM org.springframework.beans.factory.support.DefaultListableBeanFactory preInstantiateSingletons
    22:21:21 web.1     | INFO: Pre-instantiating singletons in org.springframework.beans.factory.support.DefaultListableBeanFactory@4b0ab323: defining beans [org.springframework.context.annotation.internalConfigurationAnnotationProcessor,org.springframework.context.annotation.internalAutowiredAnnotationProcessor,org.springframework.context.annotation.internalRequiredAnnotationProcessor,org.springframework.context.annotation.internalCommonAnnotationProcessor,springConfig,getJedisPool]; root of factory hierarchy
    22:21:21 web.1     | Setting up new RedisPool for connection redis://:@localhost:6379
    22:21:21 web.1     | 2011-11-20 22:21:21.707:INFO:oejs.Server:jetty-7.5.4.v20111024
    22:21:21 web.1     | 2011-11-20 22:21:21.810:INFO:oejsh.ContextHandler:started o.e.j.s.ServletContextHandler{/,null}
    22:21:21 web.1     | 2011-11-20 22:21:21.838:INFO:oejs.AbstractConnector:Started SelectChannelConnector@0.0.0.0:5000 STARTING

Test it with curl. Set a new value:

    :::term
    $ curl http://localhost:5000/mykey -d "value=myvalue"
    /mykey = myvalue

and get the value

    :::term
    $ curl http://localhost:5000/mykey
    /mykey = myvalue


You can switch between the Java and XML based configuration by commenting out one of the two lines in `Main.java` in the `spring` sub-package:

    :::java
    public class Main {

        public static void main(String[] args) throws Exception{

            // If you want Java based configuration:
    		final ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
    	
    		// If you want XML based configuration:
    		//final ApplicationContext ctx = new GenericXmlApplicationContext("applicationContext.xml");
        
            ...

