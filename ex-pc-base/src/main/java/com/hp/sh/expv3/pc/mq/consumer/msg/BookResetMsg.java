package com.hp.sh.expv3.pc.mq.consumer.msg;

public class BookResetMsg extends BaseSymbolMsg{

	public BookResetMsg(String asset, String symbol) {
		this.asset = asset;
		this.symbol = symbol;
	}
}
