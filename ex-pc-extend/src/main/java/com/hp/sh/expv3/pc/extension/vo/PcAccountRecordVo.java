/**
 * @author zw
 * @date 2019/7/19
 */
package com.hp.sh.expv3.pc.extension.vo;

import com.hp.sh.expv3.base.entity.BaseRecordEntity;

import java.math.BigDecimal;

/**
 * 永续合约_账户明细
 * 
 * @author lw
 *
 */
public class PcAccountRecordVo extends BaseRecordEntity	 {

	private static final long serialVersionUID = 1L;

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

	//交易类型：1-资金转入，2-资金转出，3-下订单，4-撤单，4-追加保证金，5-平仓收益
	private Integer tradeType;

	//序号 @see PcAccount#version
	private Long serialNo;

	//关联对象的ID
	private Long associatedId;

	public PcAccountRecordVo() {
	}

	public String getAsset() {
		return asset;
	}

	public void setAsset(String asset) {
		this.asset = asset;
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

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public Long getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(Long serialNo) {
		this.serialNo = serialNo;
	}

	public Long getAssociatedId() {
		return associatedId;
	}

	public void setAssociatedId(Long associatedId) {
		this.associatedId = associatedId;
	}

	public String toValueString() {
		return "[type=" + type + ", asset=" + asset + ", amount=" + amount + ", balance=" + balance
				+ ", tradeNo=" + tradeNo + ", tradeType=" + tradeType + ", remark=" + remark + ", userId=" + this.getUserId()
				+ "]";
	}
	
}
