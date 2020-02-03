package com.hp.sh.expv3.bb.vo.response;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;

/**
 * 修改保证金
 * @author wangjg
 *
 */
public class ChangeMarginVo {
	
	@ApiModelProperty("多空")
	private Integer longFlag;
	
	@ApiModelProperty("最多可增加保证金")
	private BigDecimal maxIncreaseMargin;
	
	@ApiModelProperty("最多可减少保证金")
	private BigDecimal maxReducedMargin;

	@ApiModelProperty("是否自动追加保证金标识")
	private Integer autoAddFlag;

	public ChangeMarginVo() {
	}

	public Integer getLongFlag() {
		return longFlag;
	}

	public void setLongFlag(Integer longFlag) {
		this.longFlag = longFlag;
	}

	public BigDecimal getMaxIncreaseMargin() {
		return maxIncreaseMargin;
	}

	public void setMaxIncreaseMargin(BigDecimal maxIncreaseMargin) {
		this.maxIncreaseMargin = maxIncreaseMargin;
	}

	public BigDecimal getMaxReducedMargin() {
		return maxReducedMargin;
	}

	public void setMaxReducedMargin(BigDecimal maxReducedMargin) {
		this.maxReducedMargin = maxReducedMargin;
	}

	public Integer getAutoAddFlag() {
		return autoAddFlag;
	}

	public void setAutoAddFlag(Integer autoAddFlag) {
		this.autoAddFlag = autoAddFlag;
	}

}
