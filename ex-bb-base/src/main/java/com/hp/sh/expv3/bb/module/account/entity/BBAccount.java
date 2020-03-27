package com.hp.sh.expv3.bb.module.account.entity;

import java.math.BigDecimal;

import javax.persistence.Table;
import javax.persistence.Version;

import com.hp.sh.expv3.base.entity.BaseAccountEntity;

/**
 * 币币_账户
 * @author lw
 *
 */
@Table(name="bb_account")
public class BBAccount extends BaseAccountEntity {

	private static final long serialVersionUID = 1L;
	
	//用户ID
	private Long userId;
	
	//资产类型
	private String asset;
	
	//余额(可用)
	private BigDecimal balance;
	
	//冻结余额
	private BigDecimal frozen;
	
	//冻结余额
	private BigDecimal total;
	
	//版本
	private Long version;

	public BBAccount() {
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	public BigDecimal getFrozen() {
		return frozen;
	}

	public void setFrozen(BigDecimal frozen) {
		this.frozen = frozen;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
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
		return "BBAccount [asset=" + asset + ", balance=" + balance + ", version=" + version + "]";
	}

}
