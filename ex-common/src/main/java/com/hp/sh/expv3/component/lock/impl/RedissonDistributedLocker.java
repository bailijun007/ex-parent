package com.hp.sh.expv3.component.lock.impl;

import java.util.concurrent.locks.Lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.sh.expv3.commons.lock.Locker;


public class RedissonDistributedLocker implements Locker {
	private static final Logger logger = LoggerFactory.getLogger(RedissonDistributedLocker.class);

	private long defaultLeaseTime = -1;
	
    private RedissonClient redissonClient;
    
    private String keyPrefix = "redisson:lock:";
    
    private String modulePrefix;
    
	public void setRedissonClient(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
	}

	public Lock getLock(String lockKey) {
        RLock lock = redissonClient.getLock(FULLKEY(lockKey));
        return lock;
    }

	public Lock getLock(String lockKey, long leaseTime) {
        RLock lock = redissonClient.getLock(FULLKEY(lockKey));
        return lock;
    }

	private String FULLKEY(String key){
		return keyPrefix+modulePrefix+key;
	}
	
	
	public String getKeyPrefix() {
		return keyPrefix;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	public String getModulePrefix() {
		return modulePrefix;
	}

	public void setModulePrefix(String modulePrefix) {
		this.modulePrefix = modulePrefix;
	}

	public long getDefaultLeaseTime() {
		return defaultLeaseTime;
	}

	public void setDefaultLeaseTime(long defaultLeaseTime) {
		this.defaultLeaseTime = defaultLeaseTime;
	}

}
