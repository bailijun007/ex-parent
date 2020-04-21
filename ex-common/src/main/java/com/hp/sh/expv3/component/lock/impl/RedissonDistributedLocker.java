package com.hp.sh.expv3.component.lock.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

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

	private static final long leaseTime = 10;
	
    @Autowired
    private RedissonClient redissonClient;
    
    private String keyPrefix = "redisson:lock:";
    
    @Value("${redisson.lock.module:}")
    private String modulePrefix;
    
	public Lock getLock(String lockKey) {
        RLock lock = redissonClient.getFairLock(FULLKEY(lockKey));
        return new ExRedissonLock(lock);
    }

	public void setRedissonClient(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
	}
	
	private String FULLKEY(String key){
		return keyPrefix+modulePrefix+key;
	}
	
	class ExRedissonLock extends MyRedissonLock{
		
		public ExRedissonLock(RLock rLock) {
			super(rLock);
		}
		
		@Override
		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
			return rLock.tryLock(time, leaseTime, unit);
		}
	}
}
