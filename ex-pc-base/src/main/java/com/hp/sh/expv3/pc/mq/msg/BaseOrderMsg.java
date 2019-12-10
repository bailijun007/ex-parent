package com.hp.sh.expv3.pc.mq.msg;

public class BaseOrderMsg {

	protected String asset;
	protected String symbol;
	
	public BaseOrderMsg() {
	}

	public String getAsset() {
		return asset;
	}
	public void setAsset(String asset) {
		this.asset = asset;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
}
