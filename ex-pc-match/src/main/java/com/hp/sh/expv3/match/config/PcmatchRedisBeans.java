/**
 * @author zw
 * @date 2019/7/23
 */
package com.hp.sh.expv3.match.config;

import com.hp.sh.expv3.match.config.setting.MetadataRedisSetting;
import com.hp.sh.expv3.match.config.setting.PcmatchRedisSetting;
import com.hp.sh.expv3.match.constant.PcmatchConst;
import com.hp.sh.expv3.match.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class PcmatchRedisBeans {

    @Autowired
    private PcmatchRedisSetting pcmatchRedisSetting;
    @Autowired
    private MetadataRedisSetting metadataRedisSetting;

    @Bean(name = PcmatchConst.MODULE_NAME + "RedisUtil")
    public RedisUtil getRedisUtil() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(pcmatchRedisSetting.getMaxTotal());
        config.setMaxIdle(pcmatchRedisSetting.getMaxIdle());
        config.setMinIdle(pcmatchRedisSetting.getMinIdle());
        config.setMaxWaitMillis(pcmatchRedisSetting.getMaxWaitMillis());
        config.setTestOnBorrow(pcmatchRedisSetting.isTestOnBorrow());
        config.setTestOnReturn(pcmatchRedisSetting.isTestOnReturn());
        config.setTestWhileIdle(pcmatchRedisSetting.isTestWhileIdle());

        JedisPool jedisPool = new JedisPool(config,
                pcmatchRedisSetting.getHostName(),
                pcmatchRedisSetting.getPort(),
                pcmatchRedisSetting.getTimeout(),
                StringUtils.isEmpty(pcmatchRedisSetting.getPassword()) ? null : pcmatchRedisSetting.getPassword(),
                pcmatchRedisSetting.getDatabase());
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
