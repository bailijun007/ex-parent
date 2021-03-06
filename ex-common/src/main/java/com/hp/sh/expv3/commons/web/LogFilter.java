package com.hp.sh.expv3.commons.web;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.hp.sh.expv3.commons.ctx.RequestContext;

//@WebFilter(filterName = "myLogFilter", urlPatterns = { "/api/**" })
public class LogFilter implements Filter {
	
	private static final String X_REQUEST_ID = "X-Request-Id";
	
	private String serverName;

	public LogFilter() {
		super();
	}

	public LogFilter(String serverName) {
		this.serverName = serverName;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if(filterConfig.getInitParameter("serverName")!=null){
			this.serverName = filterConfig.getInitParameter("serverName");
		}
	}

	@Override 
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest)request;
		HttpServletResponse httpResp = (HttpServletResponse)response;
//		httpReq = new MyCachedServletRequest(httpReq);
		httpResp.addHeader("X-w", serverName);
		httpResp.addHeader("w", serverName);
		
		String requestId = this.getRequestId(httpReq);
		if(StringUtils.isNotBlank(requestId)){
			requestId = this.newRequestId();
		}
		RequestContext.setRequestId(requestId);
		try{
			chain.doFilter(httpReq, response);
		}finally{
			RequestContext.setRequestId(null);
			RequestContext.setOperator(null);
		}
	}

	@Override
	public void destroy() {
		
	}
	
	private String getRequestId(HttpServletRequest httpReq){
		String requestId = httpReq.getParameter(X_REQUEST_ID);
		if(StringUtils.isBlank(requestId)){
			requestId = httpReq.getHeader(X_REQUEST_ID);
		}
		return requestId;
	}
	
	private String newRequestId(){
		return this.serverName + UUID.randomUUID().toString().replace("-", "");
	}

}
