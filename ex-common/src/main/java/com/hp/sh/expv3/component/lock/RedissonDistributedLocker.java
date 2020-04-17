package com.hp.sh.expv3.component.lock;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.commons.lock.Locker;


@ConditionalOnProperty(name="redisson.distributed.lock", havingValue="true")
@Component
public class RedissonDistributedLocker implements Locker{

    @Autowired
    private RedissonClient redissonClient;
    
    private String keyPrefix = "redisson:lock:";
    
    @Value("${redisson.lock.module:}")
    private String modulePrefix;

    public boolean lock(String lockKey, Integer timeout) {
        RLock lock = redissonClient.getLock(FULLKEY(lockKey));
        if(timeout!=null){
        	lock.lock(timeout, TimeUnit.SECONDS);
        }
        return lock.isLocked();
    }

    public boolean unlock(String lockKey) {
        RLock lock = redissonClient.getLock(FULLKEY(lockKey));
        lock.unlock();
        return true;
    }

    public boolean tryLock(String lockKey, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(FULLKEY(lockKey));
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
