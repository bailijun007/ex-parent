package com.hp.sh.expv3.component.lock.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.redisson.api.RLock;

public class MyRedissonLock implements Lock{
	
	private long leaseTime = -1;
	
	protected final RLock rLock;

	public MyRedissonLock(RLock rLock) {
		this.rLock = rLock;
	}

	public MyRedissonLock(RLock rLock, long leaseTime) {
		this.rLock = rLock;
		this.leaseTime = leaseTime;
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
	public boolean tryLock(long waitTime, TimeUnit unit) throws InterruptedException {
		if(this.leaseTime!=-1){
			return rLock.tryLock(waitTime, leaseTime, unit);
		}else{
			return rLock.tryLock(waitTime, unit);
		}
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
