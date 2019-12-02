package com.hp.sh.expv3.commons.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 跨域
 *
 */
public class CrossInterceptor implements HandlerInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(CrossInterceptor.class);
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
		response.setHeader("Access-Control-Allow-Headers", "X-Requested-With,X_Requested_With,Content-Type,Accept,Authorization,token");
		response.setHeader("Access-Control-Allow-Methods", "OPTIONS".equals(request.getMethod())?"*":request.getMethod());
		response.setHeader("Access-Control-Allow-Credentials", "true");
		
		if(StringUtils.isNotBlank(request.getHeader("Origin"))){
			response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
		}else{
			response.setHeader("Access-Control-Allow-Origin", "*");
		}
		
		if("OPTIONS".equals(request.getMethod())){
			
			return false;
		}
		
		return true;
	}
}
