/**
 * @author zw
 * @date 2019/7/23
 */
package com.hp.sh.expv3.match.config;

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
    private PcmatchRedisSetting redisSetting;

    @Bean(name = PcmatchConst.MODULE_NAME + "RedisUtil")
    public RedisUtil getRedisUtil() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(redisSetting.getMaxTotal());
        config.setMaxIdle(redisSetting.getMaxIdle());
        config.setMinIdle(redisSetting.getMinIdle());
        config.setMaxWaitMillis(redisSetting.getMaxWaitMillis());
        config.setTestOnBorrow(redisSetting.isTestOnBorrow());
        config.setTestOnReturn(redisSetting.isTestOnReturn());
        config.setTestWhileIdle(redisSetting.isTestWhileIdle());

        JedisPool jedisPool = new JedisPool(config, redisSetting.getHostName(), redisSetting.getPort(), redisSetting.getTimeout(),
                StringUtils.isEmpty(redisSetting.getPassword()) ? null : redisSetting.getPassword(),
                redisSetting.getDatabase());
        return new RedisUtil(jedisPool);
    }

}
