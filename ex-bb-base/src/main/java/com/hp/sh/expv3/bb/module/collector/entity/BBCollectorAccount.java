package com.hp.sh.expv3.bb.module.collector.entity;

import java.math.BigDecimal;

import javax.persistence.Table;
import javax.persistence.Version;

/**
 * 币币_币币手续费
 * @author lw
 *
 */
@Table(name="bb_collector_account")
public class BBCollectorAccount {

	protected Long collectorId;
	
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

	public BBCollectorAccount() {
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

	@Version
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getCollectorId() {
		return collectorId;
	}

	public void setCollectorId(Long id) {
		this.collectorId = id;
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

	@Override
	public String toString() {
		return "BBCollectorAccount [asset=" + asset + ", balance=" + balance + ", version=" + version + "]";
	}

}
