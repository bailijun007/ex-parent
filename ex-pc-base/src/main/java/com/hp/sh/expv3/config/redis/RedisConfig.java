package com.hp.sh.expv3.config.redis;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import com.gitee.hupadev.commons.cache.RedisCache;
import com.gitee.hupadev.commons.cache.RedisPool;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
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
		cacheNames.add("userinfo");

		Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
		configMap.put("userinfo", config.entryTtl(Duration.ofSeconds(30)));

		RedisCacheManager cacheManager = RedisCacheManager.builder(factory).cacheDefaults(config)
				.initialCacheNames(cacheNames).withInitialCacheConfigurations(configMap).build();

		return cacheManager;
	}

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory) {

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(jackson2JsonRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashKeySerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Primary
    @Bean("cf0")
    public RedisConnectionFactory redisConnectionFactory0() {
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
        conf.setHostName("192.168.0.68");
        RedisPassword password = RedisPassword.of("456");
        conf.setPassword(password);
        conf.setPort(16375);
        LettuceConnectionFactory cf = new LettuceConnectionFactory(conf);
        cf.setDatabase(0);
        return cf;
    }

    @Bean("cf5")
    public RedisConnectionFactory redisConnectionFactory5() {
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
        conf.setHostName("192.168.0.68");
        RedisPassword password = RedisPassword.of("456");
        conf.setPassword(password);
        conf.setPort(16375);
        LettuceConnectionFactory cf = new LettuceConnectionFactory(conf);
        cf.setDatabase(5);
        return cf;
    }

    @Bean(name = "templateDB0")
    public StringRedisTemplate template0(@Qualifier("cf0") RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean(name = "templateDB5")
    public StringRedisTemplate template5(@Qualifier("cf5") RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
