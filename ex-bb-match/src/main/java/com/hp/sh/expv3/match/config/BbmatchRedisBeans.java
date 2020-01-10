/**
 * @author zw
 * @date 2019/7/23
 */
package com.hp.sh.expv3.match.config;

import com.hp.sh.expv3.match.config.setting.MetadataRedisSetting;
import com.hp.sh.expv3.match.config.setting.BbmatchRedisSetting;
import com.hp.sh.expv3.match.constant.BbmatchConst;
import com.hp.sh.expv3.match.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class BbmatchRedisBeans {

    @Autowired
    private BbmatchRedisSetting bbmatchRedisSetting;
    @Autowired
    private MetadataRedisSetting metadataRedisSetting;

    @Bean(name = BbmatchConst.MODULE_NAME + "RedisUtil")
    public RedisUtil getRedisUtil() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(bbmatchRedisSetting.getMaxTotal());
        config.setMaxIdle(bbmatchRedisSetting.getMaxIdle());
        config.setMinIdle(bbmatchRedisSetting.getMinIdle());
        config.setMaxWaitMillis(bbmatchRedisSetting.getMaxWaitMillis());
        config.setTestOnBorrow(bbmatchRedisSetting.isTestOnBorrow());
        config.setTestOnReturn(bbmatchRedisSetting.isTestOnReturn());
        config.setTestWhileIdle(bbmatchRedisSetting.isTestWhileIdle());

        JedisPool jedisPool = new JedisPool(config,
                bbmatchRedisSetting.getHostName(),
                bbmatchRedisSetting.getPort(),
                bbmatchRedisSetting.getTimeout(),
                StringUtils.isEmpty(bbmatchRedisSetting.getPassword()) ? null : bbmatchRedisSetting.getPassword(),
                bbmatchRedisSetting.getDatabase());
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
