package com.hp.sh.expv3.bb.msg;

public class BaseSymbolMsg {

	//资产
	protected String asset;
	
	//交易对（合约品种）
	protected String symbol;
	
	public BaseSymbolMsg() {
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
