package com.hp.sh.expv3.pc.module.riskfund.entity;

import java.math.BigDecimal;

import javax.persistence.Table;

import com.hp.sh.expv3.base.entity.BaseSysEntity;

/**
 * 风险基金账户
 * @author lw
 *
 */
@Table(name="pc_riskfund_account")
public class PcRiskfundAccount extends BaseSysEntity{

	//资产类型
	private String asset;
	
	//余额
	private BigDecimal balance;
	
	//版本
	private Long version;

	public PcRiskfundAccount() {
	}

	public String getAsset() {
		return asset;
	}

	public void setAsset(String asset) {
		this.asset = asset;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

}
