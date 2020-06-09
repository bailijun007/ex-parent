/**
 * 
 */
package com.hp.sh.expv3.pj.module.sys.action.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author wangjg
 */
@ApiModel(value = "区号")
public class NationCodeVo {
	@ApiModelProperty(value = "区号")
	private String code;

	@ApiModelProperty(value = "地区名称")
	private String name;
	
	@ApiModelProperty(value = "英文名称")
	private String enName;

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
	
}
