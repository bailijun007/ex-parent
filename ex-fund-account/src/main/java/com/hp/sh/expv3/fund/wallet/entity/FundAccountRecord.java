package com.hp.sh.expv3.fund.wallet.entity;

import java.math.BigDecimal;

import com.hp.sh.expv3.base.entity.BaseAccountRecordEntity;

/**
 * 账变明细
 * @author wangjg
 */
public class FundAccountRecord extends BaseAccountRecordEntity {
	private static final long serialVersionUID = 1L;
	
	private Long userId;
	
	//资产
	private String asset;
	
	//流水号
	private String sn;

	//类型：1 收入,-1 支出
	private Integer type;
	
	//本笔金额
	private BigDecimal amount;
	
	//备注
	private String remark;
	
	//本笔余额
	private BigDecimal balance;
	
	//调用方支付单号
	private String tradeNo;
	
	//交易类型：1-充值，2-消费 @see #TRADETYPE_*
	private Integer tradeType;
	
	//序号 @see FundAccount#version
	private Long serialNo;
	
	public FundAccountRecord() {
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getAsset() {
		return asset;
	}

	public void setAsset(String asset) {
		this.asset = asset;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
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

	public Long getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(Long serialNo) {
		this.serialNo = serialNo;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String toValueString() {
		return "[type=" + type + ", asset=" + asset + ", amount=" + amount
				+ ", tradeNo=" + tradeNo + ", tradeType=" + tradeType + ", remark=" + remark + ", userId=" + this.getUserId()
				+ "]";
	}

	@Override
	public String toString() {
		return "FundAccountRecord [sn=" + sn + ", type=" + type + ", asset=" + asset + ", amount=" + amount
				+ ", balance=" + balance + ", tradeNo=" + tradeNo + ", tradeType=" + tradeType + ", remark=" + remark
				+ ", serialNo=" + serialNo + "]";
	}

}
