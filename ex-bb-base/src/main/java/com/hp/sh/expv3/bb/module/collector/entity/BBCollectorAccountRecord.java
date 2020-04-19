package com.hp.sh.expv3.bb.module.collector.entity;

import java.math.BigDecimal;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hp.sh.expv3.base.entity.BaseSysEntity;
import com.hp.sh.expv3.commons.mybatis.TxId;
import com.hp.sh.expv3.component.id.utils.GeneratorName;

/**
 * 币币手续费_账户明细
 * 
 * @author lw
 *
 */
@Table(name="bb_collector_account_record")
public class BBCollectorAccountRecord extends BaseSysEntity	 {

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
	
	/*
	 * @see BBAccountTradeType#*
	 * 交易类型：1-资金转入，2-资金转出，3-下订单，4-撤单，4-追加保证金，5-平仓收益
	 */
	private Integer tradeType;

	//序号 @see BBCollectorAccount#version
	private Long serialNo;

	//关联对象的ID
	private Long associatedId;

	//事务ID
	private Long txId;
	
	private Long collectorId;
	
	private String requestId;
	
	//用户ID
	private Long userId;

	public BBCollectorAccountRecord() {
	}
	
	@Id
	@GeneratedValue(generator=GeneratorName.SNOWFLAKE)
	public Long getId() {
		return id;
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

	@TxId
	public Long getTxId() {
		return txId;
	}

	public void setTxId(Long txId) {
		this.txId = txId;
	}

	public Long getCollectorId() {
		return collectorId;
	}

	public void setCollectorId(Long collectorId) {
		this.collectorId = collectorId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String toValueString() {
		return "[type=" + type + ", asset=" + asset + ", amount=" + amount.stripTrailingZeros().toPlainString() + ", tradeNo=" 
				+ tradeNo + ", tradeType=" + tradeType + ", remark=" + remark + ", userId=" + this.getUserId()
				+ "]";
	}
	
	@Override
	public String toString() {
		return "BBCollectorAccountRecord [asset=" + asset + ", sn=" + sn + ", type=" + type + ", amount=" + amount
				+ ", remark=" + remark + ", balance=" + balance + ", tradeNo=" + tradeNo + ", tradeType=" + tradeType
				+ ", serialNo=" + serialNo + ", associatedId=" + associatedId + ", txId=" + txId + ", collectorId="
				+ collectorId + ", requestId=" + requestId + ", userId=" + userId + ", id=" + id + ", created="
				+ created + ", modified=" + modified + "]";
	}
	
}
