package com.hp.sh.expv3.config.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.hp.sh.expv3.commons.ctx.RequestContext;

/**
 * @author wangjg
 *
 */
@Component
public class FAContextInterceptor implements HandlerInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(FAContextInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String operator = request.getParameter("operator");
		RequestContext.setOperator(operator);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		RequestContext.reset();
	}
}
