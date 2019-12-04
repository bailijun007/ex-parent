package com.hp.sh.expv3.pc.module.order.mq.msg;

public class BookResetMsg {

	private String asset;
	private String symbol;
	
	public BookResetMsg() {
	}
	public BookResetMsg(String asset, String symbol) {
		this.asset = asset;
		this.symbol = symbol;
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
