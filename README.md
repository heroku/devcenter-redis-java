## Using Redis from Java

Jedis is a popular Java Redis client. In order to use Jedis in your project you have to declare the dependency in your build and initialize the connection from the environment variable that Heroku provides to your application.

### Add Jedis to Your Pom.xml

Add the following dependency to your pom.xml in order to use Jedis to connect to Redis:

    <dependency>
        <groupId>redis.clients</groupId>
        <artifactId>jedis</artifactId>
        <version>2.0.0</version>
    </dependency>

### Use Redis in Your Application

    :::java
    Pattern urlPattern = Pattern.compile("^redis://([^:]*):([^@]*)@([^:]*):([^/]*)(/)?");
    //Parse the configuration URL
    Matcher matcher = urlPattern.matcher(System.getenv("REDISTOGO_URL"));
    matcher.matches();
	
    //Create the connection pool from the values in the URL
    //This pool can be stored in a singleton and reused
    JedisPool pool = new JedisPool(new Config(), matcher.group(3), Integer.parseInt(matcher.group(4)), Protocol.DEFAULT_TIMEOUT, matcher.group(2));
    Jedis jedis = pool.getResource();
    jedis.set("testKey", "test");

### Using Redis with Spring

When using Redis with Spring you can create a bean that will hold your Redis configuration and then use Spring to initialize that bean:

Redis Configuration Bean:

    public class RedisConfig {
        private String host;
        private int port;
        private String password;

        //getters and setters ommitted
    }

This bean can be initialized with either Java or XML based spring configuration:

Java Configuration:

    :::java
    @Configuration
    public class SpringConfig {
        @Bean
        public RedisConfig getRedisConfig() {
            Pattern pattern = Pattern.compile("^redis://([^:]*):([^@]*)@([^:]*):([^/]*)(/)?");
            //Parse the configuration URL
            Matcher matcher = pattern.matcher(System.getenv("REDISTOGO_URL"));
            matcher.matches();
        
            RedisConfig config = new RedisConfig();
            config.setHost(matcher.group(3));
            config.setPort(Integer.parseInt(matcher.group(4)));
            config.setPassword(matcher.group(2));
            return config;
        }
    }

or XML Configuration:

    <bean class="com.heroku.devcenter.spring.RedisConfig">
      <property name="host" value="#{systemEnvironment['REDISTOGO_URL'].replaceAll('^redis://([^:]*):([^@]*)@([^:]*):([^/]*)(/)?','$3') }"/>
      <property name="port" value="#{systemEnvironment['REDISTOGO_URL'].replaceAll('^redis://([^:]*):([^@]*)@([^:]*):([^/]*)(/)?','$4') }"/>
      <property name="password" value="#{systemEnvironment['REDISTOGO_URL'].replaceAll('^redis://([^:]*):([^@]*)@([^:]*):([^/]*)(/)?','$2') }"/>
    </bean>

Pool Creation:

    :::java
    //Use GenericXmlApplicationContext for xml based config
    ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
    RedisConfig config = ctx.getBean(RedisConfig.class);
    JedisPool pool = new JedisPool(new Config(), config.getHost(), config.getPort(), Protocol.DEFAULT_TIMEOUT, config.getPassword());
    Jedis jedis = pool.getResource();

You can also download the [sample code](http://github.com/heroku/devcenter-redis-java)
