package com.hp.sh.expv3.bb.constant;

public class MqTopic {
	
	public static final String getOrderTopic(String asset, String symbol){
		return "bbOrder_"+asset+"__"+symbol;
	}

	public static final String getMatchTopic(String asset, String symbol){
		return "bbMatch_"+asset+"__"+symbol;
	}

	
}	
