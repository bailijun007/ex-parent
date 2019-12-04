package com.hp.sh.expv3.pc.module.order.mq.utils;

public class PcMatchMqConstant {
	
	//send
	public static final String TAGS_PC_ORDER_PENDING_NEW = "PC_ORDER_PENDING_NEW";

	public static final String TAGS_ORDER_PENDING_CANCEL = "PC_ORDER_PENDING_CANCEL";

	public static final String TAGS_PC_BOOK_RESET = "PC_BOOK_RESET";
	
	public static final String getOrderTopic(String asset, String symbol){
		return "pcOrder_"+asset+"__"+symbol;
	}
	
	//consumer
	public static final String PCMATCH_BTC__BTC_USD = "pcMatch_BTC__BTC_USD";

	public static final String TAGS_NOT_MATCHED = "PC_MATCH_ORDER_NOT_MATCHED";
	
	public static final String TAGS_MATCHED = "PC_MATCH_ORDER_MATCHED";
	
	
	
}
