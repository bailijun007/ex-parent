package com.hp.sh.expv3.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author BaiLiJun  on 2020/3/6
 */
@Configuration
public class RedisTemplateConfig {

    @Value("${metadata.redis.hostName}")
    private String metadataRedisHostName;
    @Value("${metadata.redis.port}")
    private int metadataRedisPort;
    @Value("${metadata.redis.password}")
    private String metadataRedisPassword;

    @Primary
    @Bean("metadataCf0")
    public RedisConnectionFactory redisConnectionFactory0() {
        LettuceConnectionFactory cf = new LettuceConnectionFactory(standaloneConfiguration(0));
        return cf;
    }

    @Bean("metadataCf5")
    public RedisConnectionFactory redisConnectionFactory5() {
        LettuceConnectionFactory cf = new LettuceConnectionFactory(standaloneConfiguration(5));
        return cf;
    }

    private RedisStandaloneConfiguration standaloneConfiguration(int dataBase) {
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
        conf.setHostName(metadataRedisHostName);
        conf.setPort(metadataRedisPort);
        conf.setPassword(RedisPassword.of(metadataRedisPassword));
        conf.setDatabase(dataBase);
        return conf;
    }

    @Bean(name = "metadataTemplateDB0")
    public StringRedisTemplate tradeTemplate0(@Qualifier("metadataCf0") RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean(name = "metadataTemplateDB5")
    public StringRedisTemplate tradeTemplate5(@Qualifier("metadataCf5") RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

}
