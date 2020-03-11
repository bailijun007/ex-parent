package com.hp.sh.expv3.pc.module.collector.entity;

import java.math.BigDecimal;

import javax.persistence.Table;
import javax.persistence.Version;

import com.hp.sh.expv3.base.entity.BaseBizEntity;

/**
 * 币币_币币手续费
 * @author lw
 *
 */
@Table(name="pc_collector_account")
public class PcCollectorAccount extends BaseBizEntity {

	private static final long serialVersionUID = 1L;
	
	//用户ID
	private Long collectorId;
	
	//资产类型
	private String asset;
	
	//余额
	private BigDecimal balance;
	
	//版本
	private Long version;

	public PcCollectorAccount() {
	}

	public Long getCollectorId() {
		return collectorId;
	}

	public void setCollectorId(Long collectorId) {
		this.collectorId = collectorId;
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
		return "PcCollectorAccount [asset=" + asset + ", balance=" + balance + ", version=" + version + "]";
	}

}
