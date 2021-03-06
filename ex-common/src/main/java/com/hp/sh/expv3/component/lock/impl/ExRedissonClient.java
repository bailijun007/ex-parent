package com.hp.sh.expv3.component.lock.impl;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExRedissonClient {
	private static final Logger logger = LoggerFactory.getLogger(ExRedissonClient.class);

	@Value("${spring.redis.host}")
	private String redisHost;
	
	@Value("${spring.redis.port}")
	private Integer redisPort;

	@Value("${spring.redis.password}")
	private String redisPassword;
	
	@Value("${spring.redis.database:0}")
	private Integer database;
	
    public RedissonClient getRedisson() throws IOException {

        Config config = new Config();

        SingleServerConfig bc = config.useSingleServer()
                .setAddress("redis://"+redisHost+":"+redisPort)
                .setTimeout(10000)
                .setConnectTimeout(6000)
                .setConnectionPoolSize(100)
                .setConnectionMinimumIdleSize(4)
                .setDatabase(database);
        if(StringUtils.isNotBlank(redisPassword)){
        	bc.setPassword(redisPassword);
        }

        RedissonClient redisson = Redisson.create(config);

        logger.debug("redisson配置完成:"+redisson.getConfig().toJSON().toString());
        
        return redisson;
    }
    
}