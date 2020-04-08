package com.hp.sh.expv3.config.redis;

import com.hp.sh.expv3.config.redis.setting.MetadataRedisSetting;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class MetadataRedisBeans {

    @Autowired
    private MetadataRedisSetting metadataRedisSetting;


    @Bean(name = "metadataRedisUtil")
    public RedisUtil getMetadataRedisUtil() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(metadataRedisSetting.getMaxTotal());
        config.setMaxIdle(metadataRedisSetting.getMaxIdle());
        config.setMinIdle(metadataRedisSetting.getMinIdle());
        config.setMaxWaitMillis(metadataRedisSetting.getMaxWaitMillis());
        config.setTestOnBorrow(metadataRedisSetting.isTestOnBorrow());
        config.setTestOnReturn(metadataRedisSetting.isTestOnReturn());
        config.setTestWhileIdle(metadataRedisSetting.isTestWhileIdle());

        JedisPool jedisPool = new JedisPool(config,
                metadataRedisSetting.getHostName(),
                metadataRedisSetting.getPort(),
                metadataRedisSetting.getTimeout(),
                StringUtils.isEmpty(metadataRedisSetting.getPassword()) ? null : metadataRedisSetting.getPassword(),
                metadataRedisSetting.getDatabase());
        return new RedisUtil(jedisPool);
    }


}
