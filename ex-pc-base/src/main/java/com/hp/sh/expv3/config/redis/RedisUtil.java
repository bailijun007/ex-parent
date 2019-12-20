package com.hp.sh.expv3.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * redis 数据库切换
 * @author BaiLiJun  on 2019/12/19
 */
@Component
public class RedisUtil {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void setRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
    /**
     * 切换数据库
     * @param num
     */
    public void setDataBase(int num) {
        LettuceConnectionFactory connectionFactory = (LettuceConnectionFactory) stringRedisTemplate.getConnectionFactory();
        if (connectionFactory != null && num != connectionFactory.getDatabase()) {
            connectionFactory.setDatabase(num);
            this.stringRedisTemplate.setConnectionFactory(connectionFactory);
            connectionFactory.resetConnection();
        }
    }

    public StringRedisTemplate getRedisTemplate() {
        return this.stringRedisTemplate;
    }

}
