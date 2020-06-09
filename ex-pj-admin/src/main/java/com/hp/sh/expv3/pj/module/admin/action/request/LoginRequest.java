package com.hp.sh.expv3.pj.module.admin.action.request;

import com.hp.sh.expv3.pj.utils.PasswordUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Admin登陆请求")
public class LoginRequest {
	
	@ApiModelProperty(value = "用户名")
	private String username;
	
	@ApiModelProperty(value = "密码")
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String pwHex() {
		return PasswordUtils.passwdHex(password);
	}

}
