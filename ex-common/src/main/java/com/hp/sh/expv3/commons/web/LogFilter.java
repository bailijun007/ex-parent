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

import org.apache.commons.lang3.StringUtils;

@WebFilter(filterName = "myLogFilter", urlPatterns = { "/api/**" })
public class LogFilter implements Filter {
	
	private static final String X_REQUEST_ID = "X-Request-Id";
	
	private String serviceName;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.serviceName = filterConfig.getInitParameter("serviceName");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest)request;
//		httpReq = new MyCachedServletRequest(httpReq);
		
		String requestId = this.getRequestId(httpReq);
		if(StringUtils.isNotBlank(requestId)){
			requestId = this.newRequestId();
		}
		RequestContext.setRequestId(requestId);
		try{
			chain.doFilter(httpReq, response);
		}finally{
			RequestContext.setRequestId(null);
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
		return this.serviceName + UUID.randomUUID().toString().replace("-", "");
	}

}
