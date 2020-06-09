package com.hp.sh.expv3.pj.module.admin.action.request;

import com.hp.sh.expv3.pj.utils.PasswordUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "通过旧密码修改新密码")
public class ChangePasswd {
	
	@ApiModelProperty(value = "旧密码")
	private String oldPassword;
	
	@ApiModelProperty(value = "新密码")
	private String newPassword;

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String oldPwHex() {
		return PasswordUtils.passwdHex(oldPassword);
	}

	public String newPwHex() {
		return PasswordUtils.passwdHex(newPassword);
	}

}
