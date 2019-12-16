package com.hp.sh.expv3.base.entity;

/**
 * 永续合约
 * @author lw
 *
 */
public class PerpetualContract {

	//交易交易标的
	private String base;
	
	//记价单位
	private String quote;
	
	private String settle;

	public PerpetualContract() {
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public String getSettle() {
		return settle;
	}

	public void setSettle(String settle) {
		this.settle = settle;
	}

}
