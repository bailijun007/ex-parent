package com.hp.sh.expv3.fund.cash.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.hp.sh.expv3.base.entity.UserDataEntity;

/**
 * 支付记录
 * @author wangjg
 *
 */
public abstract class PaymentRecord extends UserDataEntity {
	
	private static final long serialVersionUID = 1L;
	
	//支付单号
	protected String sn;
	
	//资产
	protected String asset;
	
	//支付/收款金额
	protected BigDecimal amount;
	
	//支付渠道
	protected Integer channelId;
	
	//支付/收款账号（支付服务商端账号）
	protected String account;
	
	//支付交易ID（支付服务商端生成）
	protected String transactionId;
	
	//执行状态:0-pending，1-支付成功，2-支付失败, 3-同步余额, 4-审核中, 5-审核通过
	protected Integer payStatus;
	
	//支付状态描述
	protected String payStatusDesc;
	
	//支付完成时间
	protected Date payTime;
	
	//支付完成时间
	protected Date payFinishTime;
	
	//同步状态
	protected Integer synchStatus;

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
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

	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Integer getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}

	public String getPayStatusDesc() {
		return payStatusDesc;
	}

	public void setPayStatusDesc(String payStatusDesc) {
		this.payStatusDesc = payStatusDesc;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Date getPayFinishTime() {
		return payFinishTime;
	}

	public void setPayFinishTime(Date payFinishTime) {
		this.payFinishTime = payFinishTime;
	}

	public Integer getSynchStatus() {
		return synchStatus;
	}

	public void setSynchStatus(Integer synchStatus) {
		this.synchStatus = synchStatus;
	}

}
