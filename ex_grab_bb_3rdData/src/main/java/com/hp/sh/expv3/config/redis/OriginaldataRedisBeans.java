package com.hp.sh.expv3.config.redis;

import com.hp.sh.expv3.config.redis.setting.MetadataRedisSetting;
import com.hp.sh.expv3.config.redis.setting.OriginaldataRedisSetting;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class OriginaldataRedisBeans {

    @Autowired
    private OriginaldataRedisSetting originaldataRedisSetting;


//    @Value("${originaldata.redis.database}")

    @Bean(name = "originaldataRedisUtil")
    public RedisUtil getMetadataRedisUtil() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(originaldataRedisSetting.getMaxTotal());
        config.setMaxIdle(originaldataRedisSetting.getMaxIdle());
        config.setMinIdle(originaldataRedisSetting.getMinIdle());
        config.setMaxWaitMillis(originaldataRedisSetting.getMaxWaitMillis());
        config.setTestOnBorrow(originaldataRedisSetting.isTestOnBorrow());
        config.setTestOnReturn(originaldataRedisSetting.isTestOnReturn());
        config.setTestWhileIdle(originaldataRedisSetting.isTestWhileIdle());

        JedisPool jedisPool = new JedisPool(config,
                originaldataRedisSetting.getHostName(),
                originaldataRedisSetting.getPort(),
                originaldataRedisSetting.getTimeout(),
                StringUtils.isEmpty(originaldataRedisSetting.getPassword()) ? null : originaldataRedisSetting.getPassword(),
                originaldataRedisSetting.getDatabase());
        return new RedisUtil(jedisPool);
    }


    @Bean(name = "originaldataDb5RedisUtil")
    public RedisUtil getMetadataDb5RedisUtil() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(originaldataRedisSetting.getMaxTotal());
        config.setMaxIdle(originaldataRedisSetting.getMaxIdle());
        config.setMinIdle(originaldataRedisSetting.getMinIdle());
        config.setMaxWaitMillis(originaldataRedisSetting.getMaxWaitMillis());
        config.setTestOnBorrow(originaldataRedisSetting.isTestOnBorrow());
        config.setTestOnReturn(originaldataRedisSetting.isTestOnReturn());
        config.setTestWhileIdle(originaldataRedisSetting.isTestWhileIdle());

        JedisPool jedisPool = new JedisPool(config,
                originaldataRedisSetting.getHostName(),
                originaldataRedisSetting.getPort(),
                originaldataRedisSetting.getTimeout(),
                StringUtils.isEmpty(originaldataRedisSetting.getPassword()) ? null : originaldataRedisSetting.getPassword(),
               15);
        return new RedisUtil(jedisPool);
    }


}
