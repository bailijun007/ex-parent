package com.hp.sh.expv3.config.redis;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import com.gitee.hupadev.commons.cache.JsonCacheSerializer;
import com.gitee.hupadev.commons.cache.RedisCache;
import com.gitee.hupadev.commons.cache.RedisPool;
import com.gitee.hupadev.commons.cache.RedisPublisher;
import com.gitee.hupadev.commons.cache.RedisSubscriber;

@EnableCaching
@Configuration
public class RedisConfig {
    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

	@Primary
	@Bean
	public CacheManager cacheManager(RedisConnectionFactory factory) {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
		config = config.entryTtl(Duration.ofMinutes(10));
	
		Set<String> cacheNames = new HashSet<>();
		cacheNames.add("userinfo");
	
		Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
		configMap.put("userinfo", config.entryTtl(Duration.ofSeconds(30)));
	
		RedisCacheManager cacheManager = RedisCacheManager.builder(factory).cacheDefaults(config)
				.initialCacheNames(cacheNames).withInitialCacheConfigurations(configMap).build();
	
		return cacheManager;
	}

	@Bean
	@ConfigurationProperties(prefix = "redis")
	public RedisPool redisPool() {
		return new RedisPool();
	}

	@Lazy
	@Bean
	public RedisCache redisCache(RedisPool redisPool) {
		RedisCache rc = new RedisCache(redisPool);
		return rc;
	}
	
	@Bean
	public RedisPublisher redisPublisher(RedisPool redisPool){
		JsonCacheSerializer jsonCs = new JsonCacheSerializer();
		RedisPublisher rp = new RedisPublisher(redisPool);
		rp.setCacheSerializer(jsonCs);
		return rp;
	}
	
	@ConditionalOnProperty(name="redis.subscriber.enable", havingValue="true")
	@Bean
	public RedisSubscriber testRs(RedisPool redisPool){
		this.testRs(redisPool, "bb:account:USDT");
		
		this.testRs(redisPool, "bb:account:BTC");
		this.testRs(redisPool, "bb:order:USDT:BTC_USDT");
		this.testRs(redisPool, "bb:pos:USDT:BTC_USDT");
		this.testRs(redisPool, "bb:user:symbol:USDT:BTC_USDT");
		
		this.testRs(redisPool, "bb:account:ETH");
		this.testRs(redisPool, "bb:order:USDT:ETH_USDT");
		this.testRs(redisPool, "bb:pos:USDT:ETH_USDT");
		this.testRs(redisPool, "bb:symbol:USDT:ETH_USDT");
		return null;
	}
	
	private void testRs(RedisPool redisPool, String...channels){
		for(String channel:channels){
			RedisSubscriberTest pos = new RedisSubscriberTest(channel, redisPool);
			pos.start();
		}
	}
	
}