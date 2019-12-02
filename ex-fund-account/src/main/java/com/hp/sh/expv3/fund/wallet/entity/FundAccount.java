package com.hp.sh.expv3.fund.wallet.entity;

import java.math.BigDecimal;

import javax.persistence.Version;

import com.hp.sh.expv3.base.entity.BaseAccountEntity;

/**
 * 用户资金账户
 * @author wangjg
 */
public class FundAccount extends BaseAccountEntity {
	private static final long serialVersionUID = 1L;
	
	//用户ID
	private Long userId;

	//资产类型
	private String asset;
	
	//余额
	private BigDecimal balance;
	
	//版本
	@Version
	private Long version;
	
	public FundAccount() {
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getAsset() {
		return asset;
	}

	public void setAsset(String asset) {
		this.asset = asset;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
