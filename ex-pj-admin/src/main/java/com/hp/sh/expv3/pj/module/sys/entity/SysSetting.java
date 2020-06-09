package com.hp.sh.expv3.pj.module.sys.entity;

import com.hp.sh.expv3.pj.base.BaseBizEntity;

/**
 * 系统设置
 * @author wangjg
 */
public class SysSetting extends BaseBizEntity {

	private static final long serialVersionUID = 1L;

	// 配置名
	private String name;

	// 配置值
	private String value;

	// 数据类型：0-文本，1-布尔，3-列表,4-json
	private Integer type;

	// java类型
	private String javaType;
	
	//权限
	private String permission;

	// 备注
	private String remark;
	
	//隐藏：Y/N
	private Integer hidden;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getHidden() {
		return hidden;
	}

	public void setHidden(Integer hidden) {
		this.hidden = hidden;
	}

	@Override
	public String toString() {
		return "SysSetting [name=" + name + ", value=" + value + ", type=" + type + ", javaType=" + javaType
				+ ", permission=" + permission + ", remark=" + remark + ", hidden=" + hidden + "]";
	}

}
