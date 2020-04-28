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

	private static final long leaseTime = 30;
	
    @Autowired
    private RedissonClient redissonClient;
    
    private String keyPrefix = "redisson:lock:";
    
    @Value("${redisson.lock.module:}")
    private String modulePrefix;
    
	public void setRedissonClient(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
	}

	public Lock getLock(String lockKey) {
        RLock lock = redissonClient.getLock(FULLKEY(lockKey));
        return new ExRedissonLock(lock, leaseTime);
    }

	public Lock getLock(String lockKey, long leaseTime) {
        RLock lock = redissonClient.getLock(FULLKEY(lockKey));
        return new ExRedissonLock(lock, leaseTime);
    }

	private String FULLKEY(String key){
		return keyPrefix+modulePrefix+key;
	}
	
	class ExRedissonLock extends MyRedissonLock{
		
		private long leaseTime = -1;
		
		public ExRedissonLock(RLock rLock, long leaseTime) {
			super(rLock);
			this.leaseTime = leaseTime;
		}
		
		@Override
		public boolean tryLock(long waitTime, TimeUnit unit) throws InterruptedException {
			return rLock.tryLock(waitTime, leaseTime, unit);
		}
	}
}
