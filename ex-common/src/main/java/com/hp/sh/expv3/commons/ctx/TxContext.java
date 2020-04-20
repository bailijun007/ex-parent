package com.hp.sh.expv3.commons.ctx;

/**
 * 请求上下文
 * @author lw
 *
 */
public class TxContext {
	
	private static final ThreadLocal<Long> txIdVar = new ThreadLocal<Long>();
	
	private static final ThreadLocal<String> lockKeyHolder = new ThreadLocal<String>();
	
	public static void setTxId(Long txId){
		txIdVar.set(txId);
	}
	
	public static Long getTxId(){
		return txIdVar.get();
	}
	
	public static void setLockKey(String lockKey){
		lockKeyHolder.set(lockKey);
	}
	
	public static String getLockKey(){
		return lockKeyHolder.get();
	}
}
