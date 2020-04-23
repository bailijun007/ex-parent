package com.hp.sh.expv3.config.redis;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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

import com.gitee.hupadev.commons.cache.RedisCache;
import com.gitee.hupadev.commons.cache.RedisPool;

@EnableCaching
@Configuration
public class BBBaseCacheConfig {
    private static final Logger logger = LoggerFactory.getLogger(BBBaseCacheConfig.class);

	@Primary
	@Bean
	public CacheManager cacheManager(RedisConnectionFactory factory) {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
		config = config.entryTtl(Duration.ofMinutes(10));
	
		Set<String> cacheNames = new HashSet<>();
		cacheNames.add("order");
	
		Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
		configMap.put("order", config.entryTtl(Duration.ofSeconds(60)));
	
		RedisCacheManager cacheManager = RedisCacheManager.builder(factory).cacheDefaults(config)
				.initialCacheNames(cacheNames).withInitialCacheConfigurations(configMap).build();
	
		return cacheManager;
	}

	@Bean("baseRedisPool")
	@ConfigurationProperties(prefix = "base.redis")
	public RedisPool baseRedisPool() {
		return new RedisPool();
	}

	@Lazy
	@Bean
	public RedisCache redisCache(@Qualifier("baseRedisPool") RedisPool redisPool) {
		RedisCache rc = new RedisCache(redisPool);
		return rc;
	}
	
}