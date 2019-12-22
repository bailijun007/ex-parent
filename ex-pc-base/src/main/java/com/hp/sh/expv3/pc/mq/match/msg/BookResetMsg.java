package com.hp.sh.expv3.pc.mq.match.msg;

import com.hp.sh.expv3.pc.mq.BaseOrderMsg;

public class BookResetMsg extends BaseOrderMsg{

	public BookResetMsg(String asset, String symbol) {
		this.asset = asset;
		this.symbol = symbol;
	}
}
