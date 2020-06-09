package com.hp.sh.expv3.pj.module.admin.action;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

/**
 * 系统信息
 * 
 * @author wangjg
 */
@RestController
@RequestMapping("/sys")
public class AdminWebAction extends AdminBaseAction {
	private static final Logger logger = LoggerFactory.getLogger(AdminWebAction.class);
	
	@Value("${app.host}")
	private String appHost;

	@RequestMapping("/config.jsx")
	@ApiOperation(value = "js配置")
	public void config(HttpServletResponse response) throws IOException {
		String js = "var appConfig = {host : '"+appHost+"'}";
		response.getWriter().println(js);
	}
	
}
