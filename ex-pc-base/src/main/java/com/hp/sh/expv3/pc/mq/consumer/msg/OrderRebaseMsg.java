package com.hp.sh.expv3.pc.mq.consumer.msg;

public class OrderRebaseMsg extends BaseSymbolMsg{

	public OrderRebaseMsg(String asset, String symbol) {
		this.asset = asset;
		this.symbol = symbol;
	}
}
