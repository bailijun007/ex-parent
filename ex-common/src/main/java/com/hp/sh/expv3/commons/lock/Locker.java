package com.hp.sh.expv3.commons.lock;

public interface Locker {

	public boolean lock(String lockId, Integer timeout);
	
	public boolean unlock(String lockId);
	
}
