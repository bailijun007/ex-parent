package com.hp.sh.expv3.component.lock.impl;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.commons.lock.Locker;


@Primary
@ConditionalOnProperty(name="redisson.distributed.lock", havingValue="true")
@Component
public class RedissonDistributedLocker implements Locker {
	private static final Logger logger = LoggerFactory.getLogger(RedissonDistributedLocker.class);

    @Autowired
    private RedissonClient redissonClient;
    
    private String keyPrefix = "redisson:lock:";
    
    @Value("${redisson.lock.module:}")
    private String modulePrefix;
    
	public RLock getLock(String lockKey) {
        RLock lock = redissonClient.getFairLock(FULLKEY(lockKey));
        lock.expire(20L, TimeUnit.SECONDS);
        return lock;
    }

	public boolean lock(String lockKey, Integer waitTime) {
        RLock lock = this.getLock(lockKey);
    	try {
			lock.tryLock(waitTime, 20, TimeUnit.SECONDS);
	        return lock.isLocked();
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
    }

    @Override
	public boolean unlock(String lockKey) {
    	RLock lock = this.getLock(lockKey);
        lock.unlock();
        return true;
    }

    public boolean tryLock(String lockKey, long waitTime, long leaseTime) {
    	RLock lock = this.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

	public void setRedissonClient(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
	}
	
	private String FULLKEY(String key){
		return keyPrefix+modulePrefix+key;
	}
}
