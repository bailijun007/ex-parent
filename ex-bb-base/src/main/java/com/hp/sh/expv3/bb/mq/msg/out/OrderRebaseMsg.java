package com.hp.sh.expv3.bb.mq.msg.out;

import com.hp.sh.expv3.bb.mq.msg.BaseSymbolMsg;

public class OrderRebaseMsg extends BaseSymbolMsg{

	public OrderRebaseMsg(String asset, String symbol) {
		this.asset = asset;
		this.symbol = symbol;
	}
}
