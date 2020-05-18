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
public class ProdRedisTemplateConfig {

    @Value("${prod.metadata.redis.hostName}")
    private String metadataRedisHostName;
    @Value("${prod.metadata.redis.port}")
    private int metadataRedisPort;
    @Value("${prod.metadata.redis.password}")
    private String metadataRedisPassword;



    @Bean("prodMetadataCf5")
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



    @Bean(name = "prodMetadataTemplateDB5")
    public StringRedisTemplate tradeTemplate5(@Qualifier("prodMetadataCf5") RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

}
