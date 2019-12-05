package com.hp.sh.expv3.pc.module.order.mq.msg;

public class BookResetMsg extends BaseOrderMsg{

	public BookResetMsg(String asset, String symbol) {
		this.asset = asset;
		this.symbol = symbol;
	}
}
