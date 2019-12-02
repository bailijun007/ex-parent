package com.hp.sh.expv3.biz.vo;

/**
 * 永续合约
 * @author lw
 *
 */
public class PerpetualContract {

	//交易单位
	private String unit;
	
	//记价单位
	private String currency;

	public PerpetualContract() {
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
