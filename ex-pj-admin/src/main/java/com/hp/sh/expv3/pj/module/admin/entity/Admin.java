package com.hp.sh.expv3.pj.module.admin.entity;

import com.hp.sh.expv3.pj.base.BaseBizEntity;

/**
 * 系统设置
 * @author wangjg
 */
public class Admin extends BaseBizEntity {

	private static final long serialVersionUID = 1L;
	
	public static final int ROLE_SUPER = 1;

	public static final int ROLE_ADMIN = 2;
	
	public static final int ROLE_OPERATOR = 4;

	// 用户名
	private String username;
	
	// 登陆密码
	private String passwd;

	// 备注
	private String remark;
	
	//类型：@see #ROLE_*
	private Integer type;
	
	//启用/禁用
	private Boolean enabled;

	public Admin() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return "PlateStrategy [username=" + username + ", passwd=" + passwd + ", remark=" + remark + ", type=" + type
				+ ", enabled=" + enabled + "]";
	}

}
