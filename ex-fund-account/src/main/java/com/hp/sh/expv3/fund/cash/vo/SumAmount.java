package com.hp.sh.expv3.fund.cash.vo;

import java.math.BigDecimal;

public class SumAmount {

	private String asset;

	private BigDecimal amount;

	public SumAmount() {
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
	
}
