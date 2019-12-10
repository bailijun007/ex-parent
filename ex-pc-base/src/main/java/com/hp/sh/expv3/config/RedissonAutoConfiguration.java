package com.hp.sh.expv3.config;

import java.io.IOException;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Config.class)
public class RedissonAutoConfiguration {

    @Bean
    public  RedissonClient getRedisson() {

        //支持单机，主从，哨兵，集群等模式
        Config config = new Config();

        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6377")
                .setTimeout(2000)
                .setConnectionPoolSize(50)
                .setConnectionMinimumIdleSize(10);

        RedissonClient redisson = Redisson.create(config);

        try {
            System.out.println("检测是否配置完成:"+redisson.getConfig().toJSON().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return redisson;
    }
}