package com.hp.sh.expv3.component.lock;

public interface Lock {

	public boolean lock(String lockId, Integer timeout);
	
	public boolean unlock(String lockId);
	
}
