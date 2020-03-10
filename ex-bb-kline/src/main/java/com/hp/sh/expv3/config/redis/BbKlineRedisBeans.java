package com.hp.sh.expv3.config.redis;

import com.hp.sh.expv3.config.redis.setting.BbKlineOngoingRedisSetting;
import com.hp.sh.expv3.config.redis.setting.MetadataRedisSetting;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class BbKlineRedisBeans {

    @Autowired
    private BbKlineOngoingRedisSetting bbKlineOngoingRedisSetting;
    @Autowired
    private MetadataRedisSetting metadataRedisSetting;

    @Bean(name = "bbKlineOngoingRedisUtil")
    public RedisUtil getBbKlineOngoingExtendRedisUtil() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(bbKlineOngoingRedisSetting.getMaxTotal());
        config.setMaxIdle(bbKlineOngoingRedisSetting.getMaxIdle());
        config.setMinIdle(bbKlineOngoingRedisSetting.getMinIdle());
        config.setMaxWaitMillis(bbKlineOngoingRedisSetting.getMaxWaitMillis());
        config.setTestOnBorrow(bbKlineOngoingRedisSetting.isTestOnBorrow());
        config.setTestOnReturn(bbKlineOngoingRedisSetting.isTestOnReturn());
        config.setTestWhileIdle(bbKlineOngoingRedisSetting.isTestWhileIdle());

        JedisPool jedisPool = new JedisPool(config,
                bbKlineOngoingRedisSetting.getHostName(),
                bbKlineOngoingRedisSetting.getPort(),
                bbKlineOngoingRedisSetting.getTimeout(),
                StringUtils.isEmpty(bbKlineOngoingRedisSetting.getPassword()) ? null : bbKlineOngoingRedisSetting.getPassword(),
                bbKlineOngoingRedisSetting.getDatabase());
        return new RedisUtil(jedisPool);
    }

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
