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

Spring Configuration:

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

Pool Creation:

    :::java
    ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
    RedisConfig config = ctx.getBean(RedisConfig.class);
    JedisPool pool = new JedisPool(new Config(), config.getHost(), config.getPort(), Protocol.DEFAULT_TIMEOUT, config.getPassword());
    Jedis jedis = pool.getResource();
