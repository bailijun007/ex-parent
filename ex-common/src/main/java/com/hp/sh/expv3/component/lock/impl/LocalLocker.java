package com.hp.sh.expv3.component.lock.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.commons.lock.Locker;

@ConditionalOnProperty(name="redisson.distributed.lock", havingValue="true")
@Component
public class LocalLocker implements Locker{
	private static final Logger logger = LoggerFactory.getLogger(LocalLocker.class);
	
	private static final Map<String, ReentrantLock> lockMap = new HashMap<String, ReentrantLock>();
	
	public synchronized Lock getLock(String lockId){
		ReentrantLock lock = lockMap.get(lockId);
		if(lock==null){
			lock = new ReentrantLock(true);
			lockMap.put(lockId, lock);
		}
		return lock;
	}

}
