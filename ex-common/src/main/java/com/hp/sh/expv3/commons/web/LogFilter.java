package com.hp.sh.expv3.commons.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.gitee.hupadev.commons.id.IdGenerator;

@WebFilter(filterName = "myLogFilter", urlPatterns = { "/api/**" })
public class LogFilter implements Filter {
	
	@Autowired(required=false)
	private IdGenerator idGenerator;
	
	private String sequenceName = "LogFilter";;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest)request;
//		httpReq = new MyCachedServletRequest(httpReq);
		System.out.println(httpReq.getServletPath());
		System.out.println(httpReq.getHeader("X-Request-Id"));
		RequestContext.setRequestId(this.getRequestId());
		chain.doFilter(httpReq, response);
		RequestContext.setRequestId(null);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	private Long getRequestId(){
		if(idGenerator!=null){
			Long id = idGenerator.nextId(sequenceName);
			return id;
		}
		return null;
	}

}
