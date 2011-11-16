package com.heroku.devcenter.spring;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
