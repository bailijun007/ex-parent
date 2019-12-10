package com.hp.sh.expv3.config;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Config.class)
public class RedissonAutoConfiguration {

	@Value("${spring.redis.host}")
	private String redisHost;
	
	@Value("${spring.redis.port}")
	private Integer redisPort;

	@Value("${spring.redis.password}")
	private String redisPassword;
	
    @Bean
    public  RedissonClient getRedisson() {

        Config config = new Config();

        SingleServerConfig bc = config.useSingleServer()
                .setAddress("redis://"+redisHost+":"+redisPort)
                .setTimeout(2000)
                .setConnectionPoolSize(50)
                .setConnectionMinimumIdleSize(10);
        if(StringUtils.isNotBlank(redisPassword)){
        	bc.setPassword(redisPassword);
        }

        RedissonClient redisson = Redisson.create(config);

        try {
            System.out.println("检测是否配置完成:"+redisson.getConfig().toJSON().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return redisson;
    }
    
}