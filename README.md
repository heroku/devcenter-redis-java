## Using Redis from Java

This is an example of using Jedis to connect to the Redis To Go service from both a generic Java application and a Spring configured application on Heroku. Read more about how to use Redis To Go in the [add-on article](http://devcenter.heroku.com/articles/redistogo).

# Using The Sample Code

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
    22:31:48 sample.1        | started with pid 74251
    22:31:48 springsample.1  | started with pid 74252
    22:31:48 sample.1        | Setting up new RedisPool for connection redis://redistogo:9451749016f2bd2780a19abe20d343b8@viperfish.redistogo.com:9411/
    22:31:48 springsample.1  | Nov 21, 2011 10:31:48 PM org.springframework.context.support.AbstractApplicationContext prepareRefresh
    22:31:48 springsample.1  | INFO: Refreshing org.springframework.context.annotation.AnnotationConfigApplicationContext@95c083: startup date [Mon Nov 21 22:31:48 PST 2011]; root of context hierarchy
    22:31:48 springsample.1  | Nov 21, 2011 10:31:48 PM org.springframework.beans.factory.support.DefaultListableBeanFactory preInstantiateSingletons
    22:31:48 springsample.1  | INFO: Pre-instantiating singletons in org.springframework.beans.factory.support.DefaultListableBeanFactory@d8d9850: defining beans [org.springframework.context.annotation.internalConfigurationAnnotationProcessor,org.springframework.context.annotation.internalAutowiredAnnotationProcessor,org.springframework.context.annotation.internalRequiredAnnotationProcessor,org.springframework.context.annotation.internalCommonAnnotationProcessor,springConfig,getJedisPool]; root of factory hierarchy
    22:31:48 springsample.1  | Setting up new RedisPool for connection redis://redistogo:9451749016f2bd2780a19abe20d343b8@viperfish.redistogo.com:9411/
    22:31:49 springsample.1  | Value set into Redis is: testValueSpring
    22:31:49 sample.1        | Value set into Redis is: testValue
    22:31:49 springsample.1  | Value retrieved from Redis is: testValueSpring
    22:31:49 sample.1        | Value retrieved from Redis is: testValue
    22:31:49 springsample.1  | process terminated
    22:31:49 system          | sending SIGTERM to all processes


You can switch between the Java and XML based configuration by commenting out one of the two lines in `Main.java` in the `spring` sub-package:

    :::java
    public class Main {

        public static void main(String[] args) throws Exception{

            // If you want Java based configuration:
    		final ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
    	
    		// If you want XML based configuration:
    		//final ApplicationContext ctx = new GenericXmlApplicationContext("applicationContext.xml");
        
            ...

