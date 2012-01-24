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

Now that it's built, you can run the plain Java example, or the Spring example:

* To run the plain Java example:

    $ sh target/bin/sample

* To run the Spring example:

    $ sh target/bin/spring-sample

<div class="callout" markdown="1">
Note: you can also use foreman to execute the Procfile. [Read more about foreman and procfiles](http://devcenter.heroku.com/articles/procfile).
</div>

You can switch between the Java and XML based configuration by commenting out one of the two lines in `Main.java` in the `spring` sub-package:

    :::java
    public class Main {

        public static void main(String[] args) throws Exception{

            // If you want Java based configuration:
        	final ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
    	
    		// If you want XML based configuration:
    		//final ApplicationContext ctx = new GenericXmlApplicationContext("applicationContext.xml");
        
            ...

# Test on Heroku

Assuming you already have a [Heroku account](http://heroku.com/signup) and have installed the [Heroku command line tool](http://devcenter.heroku.com/articles/java), you can test this sample on Heroku in a few steps.

## Create Heroku App

    $ heroku create -s cedar
    Creating quiet-waterfall-6274... done, stack is cedar
    http://quiet-waterfall-6274.herokuapp.com/ | git@heroku.com:quiet-waterfall-6274.git
    Git remote heroku added

## Add Redis To Go Service

    $ heroku addons:add redistogo:nano
    -----> Adding redistogo:nano to quiet-waterfall-6274... done, v1 (free)

## Deploy Sample Using Git

    $ git push heroku master
    Counting objects: 94, done.
    Delta compression using up to 4 threads.
    Compressing objects: 100% (53/53), done.
    Writing objects: 100% (94/94), 14.58 KiB, done.
    Total 94 (delta 22), reused 57 (delta 13)

    -----> Heroku receiving push
    -----> Java app detected
    -----> Installing Maven 3.0.3..... done
    -----> Installing settings.xml..... done
    -----> executing /app/tmp/repo.git/.cache/.maven/bin/mvn -B -Duser.home=/tmp/build_2trks2rwxstiq -Dmaven.repo.local=/app/tmp/repo.git/.cache/.m2/repository -s /app/tmp/repo.git/.cache/.m2/settings.xml -DskipTests=true clean install
           [INFO] Scanning for projects...
           [INFO]                                                                         
           [INFO] ------------------------------------------------------------------------
           [INFO] Building redisSample 0.0.1-SNAPSHOT
           [INFO] ------------------------------------------------------------------------
           Downloading: http://s3pository.heroku.com/jvm/redis/clients/jedis/2.0.0/jedis-2.0.0.pom
           ...
           [INFO] ------------------------------------------------------------------------
           [INFO] BUILD SUCCESS
           [INFO] ------------------------------------------------------------------------
           [INFO] Total time: 7.055s
           [INFO] Finished at: Tue Nov 22 22:18:19 UTC 2011
           [INFO] Final Memory: 10M/490M
           [INFO] ------------------------------------------------------------------------
    -----> Discovering process types
           Procfile declares types -> sample, springsample
    -----> Compiled slug size is 2.4MB
    -----> Launching... done, v4
           http://quiet-waterfall-6274.herokuapp.com deployed to Heroku

## Execute the Sample Code as One-Off Processes

The two sample apps are listed as two process types, "sample" and "springsample". They are designed to be executed as one-off processes, so you execute them with

    $ heroku run sample
    Running sample attached to terminal... up, run.1
    Value set into Redis is: testValue
    Value retrieved from Redis is: testValue

and

    $ heroku run springsample
    Running springsample attached to terminal... up, run.2
    Nov 22, 2011 10:18:54 PM org.springframework.context.support.AbstractApplicationContext prepareRefresh
    INFO: Refreshing org.springframework.context.annotation.AnnotationConfigApplicationContext@50a9ae05: startup date [Tue Nov 22 22:18:54 UTC 2011]; root of context hierarchy
    Nov 22, 2011 10:18:54 PM org.springframework.beans.factory.support.DefaultListableBeanFactory preInstantiateSingletons
    INFO: Pre-instantiating singletons in org.springframework.beans.factory.support.DefaultListableBeanFactory@1d10c424: defining beans [org.springframework.context.annotation.internalConfigurationAnnotationProcessor,org.springframework.context.annotation.internalAutowiredAnnotationProcessor,org.springframework.context.annotation.internalRequiredAnnotationProcessor,org.springframework.context.annotation.internalCommonAnnotationProcessor,springConfig,getJedisPool]; root of factory hierarchy
    Value set into Redis is: testValueSpring
    Value retrieved from Redis is: testValueSpring
