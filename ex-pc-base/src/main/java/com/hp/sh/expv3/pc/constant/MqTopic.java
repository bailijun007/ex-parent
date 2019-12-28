package com.hp.sh.expv3.pc.constant;

public class MqTopic {
	
	//consumer
	@Deprecated
	public static final String TOPIC_PCMATCH_BTC__BTC_USD = "pcMatch_BTC__BTC_USD";

	public static final String getOrderTopic(String asset, String symbol){
		return "pcOrder_"+asset+"__"+symbol;
	}

	
}
