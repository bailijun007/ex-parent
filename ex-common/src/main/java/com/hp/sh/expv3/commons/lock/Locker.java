package com.hp.sh.expv3.commons.lock;

public interface Locker {

	/**
	 * 
	 * @param lockId
	 * @param timeout 超时时间(秒)
	 * @return
	 */
	public boolean lock(String lockId, Integer timeout);
	
	public boolean unlock(String lockId);
	
}
