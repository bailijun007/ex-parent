package com.hp.sh.expv3.config.redis;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import com.gitee.hupadev.commons.cache.RedisCache;
import com.gitee.hupadev.commons.cache.RedisPool;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@EnableCaching
@Configuration
public class RedisConfig {

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

	@Primary
	@Bean
	public CacheManager cacheManager(RedisConnectionFactory factory) {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
		config = config.entryTtl(Duration.ofMinutes(10));

		Set<String> cacheNames = new HashSet<>();
		cacheNames.add("test");

		Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
		configMap.put("test", config.entryTtl(Duration.ofSeconds(30)));

		RedisCacheManager cacheManager = RedisCacheManager.builder(factory).cacheDefaults(config)
				.initialCacheNames(cacheNames).withInitialCacheConfigurations(configMap).build();

		return cacheManager;
	}
	
    @Bean("myKeyGenerator")
    public KeyGenerator myKeyGenerator() {
    	KeyGenerator keygen = new KeyGenerator(){

			public Object generate(Object target, Method method, Object... params) {
	            StringBuilder sb = new StringBuilder();
	            sb.append(method.getDeclaringClass().getName());
	            sb.append('.');
	            sb.append(method.getName());
	            sb.append('(');
	            for (int i=0; i<params.length;i++) {
	            	Object obj = params[i];
	            	if(i>0){
	            		sb.append(",");
	            	}
	                sb.append(obj.toString());
	            }
	            sb.append(')');
	            return sb.toString();
			}
    		
    	};
    	
    	return keygen;

    }
    
    @Bean("testgk")
    public KeyGenerator testgk() {
    	KeyGenerator keygen = new KeyGenerator(){

			public Object generate(Object target, Method method, Object... params) {
	            return "testgk";
			}
    		
    	};
    	
    	return keygen;

    }

//    @Bean
//    @ConditionalOnMissingBean(name = "redisTemplate")
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
//
//        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(om);
//
//        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
//        template.setConnectionFactory(redisConnectionFactory);
//        template.setKeySerializer(jackson2JsonRedisSerializer);
//        template.setValueSerializer(jackson2JsonRedisSerializer);
//        template.setHashKeySerializer(jackson2JsonRedisSerializer);
//        template.setHashValueSerializer(jackson2JsonRedisSerializer);
//        template.afterPropertiesSet();
//        return template;
//    }
//
//
//    @Bean
//    @ConditionalOnMissingBean(StringRedisTemplate.class)
//    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        StringRedisTemplate template = new StringRedisTemplate();
//        template.setConnectionFactory(redisConnectionFactory);
//        return template;
//    }

}
