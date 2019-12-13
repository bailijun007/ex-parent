package com.hp.sh.expv3.commons.web;

/**
 * 请求上下文
 * @author lw
 *
 */
public class RequestContext {
	
	private static final ThreadLocal<Long> requestIdVar = new ThreadLocal<Long>();
	
	private static final ThreadLocal<String> operatorVar = new ThreadLocal<String>();

	public static void setRequestId(Long txId){
		requestIdVar.set(txId);
	}
	
	public static long getRequestId(){
		return requestIdVar.get();
	}

	public static void setOperator(String operator){
		operatorVar.set(operator);
	}
	
	public static String getOperator(){
		return operatorVar.get();
	}
	
	public static void reset(){
		requestIdVar.set(null);
		operatorVar.set(null);
	}
}
