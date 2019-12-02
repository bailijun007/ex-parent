package com.hp.sh.expv3.config.web;

/**
 * 请求上下文
 * @author lw
 *
 */
public class RequestContext {
	
	private static final ThreadLocal<Long> txIdVar = new ThreadLocal<Long>();
	
	private static final ThreadLocal<String> operatorVar = new ThreadLocal<String>();

	public static void setTxId(long txId){
		txIdVar.set(txId);
	}
	
	public static long getTxId(){
		return txIdVar.get();
	}

	public static void setOperator(String operator){
		operatorVar.set(operator);
	}
	
	public static String getOperator(){
		return operatorVar.get();
	}
	
	public static void reset(){
		txIdVar.set(null);
		operatorVar.set(null);
	}
}
