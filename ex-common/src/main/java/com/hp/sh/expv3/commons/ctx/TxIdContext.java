package com.hp.sh.expv3.commons.ctx;

/**
 * 请求上下文
 * @author lw
 *
 */
public class TxIdContext {
	
	private static final ThreadLocal<Long> txIdVar = new ThreadLocal<Long>();
	
	public static void setTxId(Long txId){
		txIdVar.set(txId);
	}
	
	public static Long getTxId(){
		return txIdVar.get();
	}
	
	public static void reset(){
		txIdVar.set(null);
	}

}
