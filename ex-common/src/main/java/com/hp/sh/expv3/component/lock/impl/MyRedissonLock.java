package com.hp.sh.expv3.component.lock.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.redisson.api.RLock;

public class MyRedissonLock implements Lock{
	
	protected RLock rLock;

	public MyRedissonLock(RLock rLock) {
		this.rLock = rLock;
	}

	@Override
	public void lock() {
		rLock.lock();
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		rLock.lockInterruptibly();
	}

	@Override
	public boolean tryLock() {
		return rLock.tryLock();
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		return rLock.tryLock(time, unit);
	}

	@Override
	public void unlock() {
		rLock.unlink();
	}

	@Override
	public Condition newCondition() {
		return rLock.newCondition();
	}

}
