package com.hp.sh.expv3.commons.lock;

import java.util.concurrent.locks.Lock;

public interface Locker {
	
	public Lock getLock(String lockId);
}
