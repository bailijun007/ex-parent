package com.hp.sh.expv3.bb.mq.match.msg;

import com.hp.sh.expv3.bb.msg.BaseSymbolMsg;

public class BookResetMsg extends BaseSymbolMsg{

	public BookResetMsg(String asset, String symbol) {
		this.asset = asset;
		this.symbol = symbol;
	}
}
