package com.hp.sh.expv3.pc.vo.request;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 风险基金请求
 * @author wangjg
 */
@ApiModel(value = "风险基金请求")
public class RiskFundRequest {

	@ApiModelProperty(value = "用户ID")
	private Long userId;

	@ApiModelProperty(value = "资产")
	private String asset;
	
	@ApiModelProperty(value = "本笔金额")
	private BigDecimal amount;
	
	@ApiModelProperty(value = "订单类型")
	private Integer tradeType;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "调用方支付单号")
	private String tradeNo;
	
	public RiskFundRequest() {
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getAsset() {
		return asset;
	}

	public void setAsset(String asset) {
		this.asset = asset;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public Integer getTradeType() {
		return tradeType;
	}

	public void setTradeType(Integer tradeType) {
		this.tradeType = tradeType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
