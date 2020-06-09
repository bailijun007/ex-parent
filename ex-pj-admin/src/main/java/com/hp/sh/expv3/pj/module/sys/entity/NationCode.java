/**
 * 
 */
package com.hp.sh.expv3.pj.module.sys.entity;

import com.hp.sh.expv3.pj.base.BaseBizEntity;

/**
 * 手机地区码
 * @author wangjg
 */
public class NationCode extends BaseBizEntity {
	
	private static final long serialVersionUID = 1L;

	//区号
	private String code;

	//地区名称
	private String name;
	
	//英文名称
	private String enName;
	
	//是否可用 0/1
	private Integer enabled;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEnName() {
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}
	
}
