package com.hp.sh.expv3.commons.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 请求上下文
 * @author lw
 *
 */
public class RequestContext {
	
	private static final ThreadLocal<String> requestIdVar = new ThreadLocal<String>();
	
	private static final ThreadLocal<String> operatorVar = new ThreadLocal<String>();

	public static void setRequestId(String requestId){
		requestIdVar.set(requestId);
	}
	
	public static String getRequestId(){
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
