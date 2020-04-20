package com.hp.sh.expv3.component.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.sh.expv3.commons.lock.Locker;

public class LocalLock  implements Locker{
	private static final Logger logger = LoggerFactory.getLogger(LocalLock.class);
	
	private static final Map<String, ReentrantLock> lockMap = new HashMap<String, ReentrantLock>();

	@Override
	public boolean lock(String lockId, Integer timeout) {
		Lock lock = this.getLock(lockId);
		try {
			lock.tryLock(timeout, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	@Override
	public boolean unlock(String lockId) {
		Lock lock = this.getLock(lockId);
		lock.unlock();
		return true;
	}
	
	public synchronized ReentrantLock getLock(String lockId){
		ReentrantLock lock = lockMap.get(lockId);
		if(lock==null){
			lock = new ReentrantLock();
			lockMap.put(lockId, lock);
		}
		return lock;
	}

}
