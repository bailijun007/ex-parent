package com.hp.sh.expv3.config.redis;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitee.hupadev.commons.cache.CacheSerializer;
import com.gitee.hupadev.commons.cache.JsonCacheSerializer;
import com.gitee.hupadev.commons.cache.RedisCache;
import com.gitee.hupadev.commons.cache.RedisPool;

@EnableCaching
@Configuration
public class BaseCacheConfig {
    private static final Logger logger = LoggerFactory.getLogger(BaseCacheConfig.class);
    
    @Autowired
    private ObjectMapper objectMapper;

	@Primary
	@Bean
	public CacheManager cacheManager(RedisConnectionFactory factory) {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
		
//		config = config.serializeValuesWith(getSp());
		
		config = config.entryTtl(Duration.ofMinutes(10));
		config = config.computePrefixWith(name -> "project:"+name + ":");
	
		Set<String> cacheNames = new HashSet<>();
		cacheNames.add("order");
		cacheNames.add("errorMsg");
		cacheNames.add("shardOffset");
	
		Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
		configMap.put("order", config.entryTtl(Duration.ofSeconds(60)));
		configMap.put("errorMsg", config.entryTtl(Duration.ZERO));
		configMap.put("shardOffset", config.entryTtl(Duration.ZERO));
	
		RedisCacheManager cacheManager = RedisCacheManager.builder(factory).cacheDefaults(config)
				.initialCacheNames(cacheNames).withInitialCacheConfigurations(configMap).build();
	
		return cacheManager;
	}
	
	private SerializationPair getSp(){
        Jackson2JsonRedisSerializer rs = new Jackson2JsonRedisSerializer(Object.class);
 
        //解决查询缓存转换异常的问题
//        ObjectMapper om = new ObjectMapper();
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        rs.setObjectMapper(objectMapper);
        
		SerializationPair<Object> sp = RedisSerializationContext.SerializationPair.fromSerializer(rs);
		return sp;
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
		rc.setDbIndex(15);
		CacheSerializer cacheSerializer = new JsonCacheSerializer();
		rc.setCacheSerializer(cacheSerializer);
		return rc;
	}
	
}