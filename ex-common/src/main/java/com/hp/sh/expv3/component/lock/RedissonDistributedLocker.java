package com.hp.sh.expv3.component.lock;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.commons.lock.Locker;


@Component
public class RedissonDistributedLocker implements Locker{

    @Autowired
    private RedissonClient redissonClient;

    public boolean lock(String lockKey, Integer timeout) {
        RLock lock = redissonClient.getLock(lockKey);
        if(timeout!=null){
        	lock.lock(timeout, TimeUnit.SECONDS);
        }
        return lock.isLocked();
    }

    public boolean unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.unlock();
        return true;
    }

    public boolean tryLock(String lockKey, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }
}
