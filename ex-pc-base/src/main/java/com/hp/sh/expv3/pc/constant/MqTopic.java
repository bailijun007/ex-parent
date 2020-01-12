package com.hp.sh.expv3.pc.constant;

public class MqTopic {
	
	public static final String getOrderTopic(String asset, String symbol){
		return "pcOrder_"+asset+"__"+symbol;
	}

	public static final String getMatchTopic(String asset, String symbol){
		return "pcMatch_"+asset+"__"+symbol;
	}

	
}	
