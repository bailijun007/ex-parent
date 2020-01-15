package com.hp.sh.expv3.fund.cash.api.vo;

public class BysCreateResult {
	
	private String deposit_order_id;

	public BysCreateResult(String deposit_order_id) {
		this.deposit_order_id = deposit_order_id;
	}

	public String getDeposit_order_id() {
		return deposit_order_id;
	}

	public void setDeposit_order_id(String deposit_order_id) {
		this.deposit_order_id = deposit_order_id;
	}
	
}
