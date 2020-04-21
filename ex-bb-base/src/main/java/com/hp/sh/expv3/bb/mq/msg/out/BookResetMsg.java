package com.hp.sh.expv3.bb.mq.msg.out;

import com.hp.sh.expv3.bb.mq.msg.BaseSymbolMsg;

public class BookResetMsg extends BaseSymbolMsg{

	public BookResetMsg(String asset, String symbol) {
		this.asset = asset;
		this.symbol = symbol;
	}
}
