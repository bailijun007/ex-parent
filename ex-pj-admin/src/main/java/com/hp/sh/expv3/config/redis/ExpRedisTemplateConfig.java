package com.hp.sh.expv3.config.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * base
 * @author wangjg
 *
 */
@Configuration
public class ExpRedisTemplateConfig {

	@Value("${exp.redis.host}")
	private String hostName;
	@Value("${exp.redis.port}")
	private Integer port;
	@Value("${exp.redis.password}")
	private String password;

    @Bean("cf0")
    public RedisConnectionFactory redisConnectionFactory0() {
        LettuceConnectionFactory cf = new LettuceConnectionFactory(standaloneConfiguration(0));
        return cf;
    }

    @Primary
    @Bean("cf15")
    public RedisConnectionFactory redisConnectionFactory5() {
        LettuceConnectionFactory cf = new LettuceConnectionFactory(standaloneConfiguration(15));
        return cf;
    }

	private RedisStandaloneConfiguration standaloneConfiguration(int dataBase){
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
        conf.setHostName(hostName);
        conf.setPort(port);
        conf.setPassword(RedisPassword.of(password));
        conf.setDatabase(dataBase);
        return conf;
	}

    @Bean(name = "templateDB0")
    public StringRedisTemplate template0(@Qualifier("cf0") RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean(name = "templateDB15")
    public StringRedisTemplate template5(@Qualifier("cf15") RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
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
}
