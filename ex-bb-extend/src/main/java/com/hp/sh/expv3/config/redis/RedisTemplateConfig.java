package com.hp.sh.expv3.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@Configuration
public class RedisTemplateConfig {

	@Value("${meta.redis.host}")
	private String hostName;
	@Value("${meta.redis.port}")
	private Integer port;
	@Value("${meta.redis.password}")
	private String password;


    @Primary
    @Bean("cf0")
    public RedisConnectionFactory redisConnectionFactory0() {
        LettuceConnectionFactory cf = new LettuceConnectionFactory(standaloneConfiguration(0));
        return cf;
    }

    @Bean("cf5")
    public RedisConnectionFactory redisConnectionFactory5() {
        LettuceConnectionFactory cf = new LettuceConnectionFactory(standaloneConfiguration(5));
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



    @Bean(name = "templateDB5")
    public StringRedisTemplate template5(@Qualifier("cf5") RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

}
