package com.hp.sh.expv3.config.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.gitee.hupadev.base.exceptions.BizException;
import com.gitee.hupadev.base.exceptions.CommonError;
import com.gitee.hupadev.base.web.WebUtils;
import com.hp.sh.expv3.pj.base.WebSessionService;
import com.hp.sh.expv3.pj.module.admin.entity.Admin;
import com.hp.sh.expv3.pj.module.admin.service.AdminService;

/**
 * @author wangjg
 *
 */
@Component
public class AdminAuthInterceptor extends WebSessionService implements HandlerInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(AdminAuthInterceptor.class);
	@Autowired
	protected AdminService adminService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Admin admin = this.loginAdmin();
		if(admin==null){
			if(WebUtils.isJsonResult(request, response)){
				throw new BizException(CommonError.AUTH);
			}else{
				this.sendRedirect(response, "/admin/login.html");
				return false;
			}
		}
		return true;
	}
	
	protected Admin loginAdmin(){
		Long adminId = this.currentUserId();
		Admin admin = adminService.getAdmin(adminId);
		return admin;
	}
	
	protected String userCacheKey(String sid){
		return "pj:admin-login::"+sid;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}
	
	protected void sendRedirect(HttpServletResponse response, String location) {
		try {
			response.sendRedirect(location);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
