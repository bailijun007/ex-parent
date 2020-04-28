package com.hp.sh.expv3.config.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gitee.hupadev.commons.cache.JsonCacheSerializer;
import com.gitee.hupadev.commons.cache.RedisPool;
import com.gitee.hupadev.commons.cache.RedisPublisher;

@Configuration
public class ExpPublisherConfig {
    private static final Logger logger = LoggerFactory.getLogger(ExpPublisherConfig.class);

    @Bean("expRedisPool")
	@ConfigurationProperties(prefix = "exp.redis")
	public RedisPool expRedisPool() {
		return new RedisPool();
	}
    
	@Bean
	public RedisPublisher redisPublisher(@Qualifier("expRedisPool") RedisPool redisPool){
		JsonCacheSerializer jsonCs = new JsonCacheSerializer();
		RedisPublisher rp = new RedisPublisher(redisPool);
		rp.setCacheSerializer(jsonCs);
		return rp;
	}
	
}