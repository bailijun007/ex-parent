package com.hp.sh.expv3.pc.module.riskfund.entity;

import java.math.BigDecimal;

import javax.persistence.Table;

/**
 * 风险基金账户
 * @author lw
 *
 */
@Table(name="pc_riskfund_account")
public class PcRiskfundAccount {

	protected Long id;
	
	//资产类型
	private String asset;
	
	//余额
	private BigDecimal balance;

	// 创建时间
	private Long created;
	
	// 修改时间
	private Long modified;
	
	//版本
	private Long version;

	public PcRiskfundAccount() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public Long getModified() {
		return modified;
	}

	public void setModified(Long modified) {
		this.modified = modified;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

}
