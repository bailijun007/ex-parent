package com.hp.sh.expv3.pj.base;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.gitee.hupadev.base.exceptions.BizException;
import com.gitee.hupadev.base.exceptions.CommonError;
import com.gitee.hupadev.base.web.WebUtils;
import com.gitee.hupadev.commons.cache.Cache;

/**
 * 
 * @author wangjg
 *
 */
public class WebSessionService {

	@Autowired(required = false)
	protected Cache cache;

	protected Long currentUserId() {
		String sid = this.getXToken();
		Object cv = cache.get(userCacheKey(sid));
		if(cv==null){
			return null;
		}
		Long userId = Long.valueOf(""+cv);
		if(userId==null){
			throw new BizException(CommonError.AUTH);
		}
		return userId;
	}
	
	protected String userCacheKey(String sid){
		return "login::"+sid;
	}
	
	protected String getXToken(){
		String sid = null;
		if(StringUtils.isBlank(sid)){
			sid = this.getRequest().getParameter("X-TOKEN");
		}
		if(StringUtils.isBlank(sid)){
			sid = this.getRequest().getHeader("X-TOKEN");
		}
		if(StringUtils.isBlank(sid)){
			sid = WebUtils.getCookie(this.getRequest(), "X-TOKEN");
		}
		return sid;
	}
	
	protected HttpServletRequest getRequest(){
		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
		return request;
	}
	
}
