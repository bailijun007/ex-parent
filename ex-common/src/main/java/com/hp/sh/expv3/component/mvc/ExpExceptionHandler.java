/**
 * 
 */
package com.hp.sh.expv3.component.mvc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.gitee.hupadev.base.exceptions.ExceptionUtils;
import com.gitee.hupadev.base.feign.FeignPreInterceptor;
import com.gitee.hupadev.base.spring.aop.GlobalExceptionHandler;

/**
 * @author wangjg
 */
@ControllerAdvice
public class ExpExceptionHandler extends GlobalExceptionHandler{
	private static final Logger logger = LoggerFactory.getLogger(ExpExceptionHandler.class);

    public ResponseEntity<Map<String, Object>> getResponse(HttpServletRequest request, HttpServletResponse response, Exception exception){
    	response.setHeader("x-eh", "1");
		Map<String,Object> err = ExceptionUtils.getError(exception);
		Integer code = (Integer) err.get("code");
		if(code>0){
			code = code * -1;
			err.put("code", code);
		}
		HttpStatus httpStatus = HttpStatus.OK;
		if(StringUtils.isNotBlank(request.getHeader(FeignPreInterceptor.FEIGN_CLIENT_HEADER))){
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
    	return new ResponseEntity<Map<String, Object>>(err, httpStatus);
    }

}
