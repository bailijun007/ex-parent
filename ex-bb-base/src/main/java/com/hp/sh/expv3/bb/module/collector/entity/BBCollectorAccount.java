package com.hp.sh.expv3.bb.module.collector.entity;

import java.math.BigDecimal;

import javax.persistence.Table;
import javax.persistence.Version;

import com.hp.sh.expv3.base.entity.BaseSysEntity;

/**
 * 币币_币币手续费
 * @author lw
 *
 */
@Table(name="bb_collector_account")
public class BBCollectorAccount extends BaseSysEntity{

	//资产类型
	private String asset;
	
	//余额
	private BigDecimal balance;

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

	@Override
	public String toString() {
		return "BBCollectorAccount [asset=" + asset + ", balance=" + balance + ", version=" + version + ", id=" + id
				+ ", created=" + created + ", modified=" + modified + "]";
	}


}
