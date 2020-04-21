package com.hp.sh.expv3.bb.mq.msg;

import com.hp.sh.rocketmq.msg.AbstractRocketMsg;

public class BaseSymbolMsg extends AbstractRocketMsg{

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
