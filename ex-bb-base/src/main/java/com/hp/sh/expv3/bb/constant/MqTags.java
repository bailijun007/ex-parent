package com.hp.sh.expv3.bb.constant;

public class MqTags {
	
	//send
	public static final String TAGS_ORDER_PENDING_NEW = "BB_ORDER_PENDING_NEW";

	public static final String TAGS_ORDER_PENDING_CANCEL = "BB_ORDER_PENDING_CANCEL";

	public static final String TAGS_BOOK_RESET = "BB_BOOK_RESET";
	
	public static final String TAGS_ORDER_REBASE = "BB_ORDER_REBASE";
	
	//consumer
	public static final String TAGS_CANCELLED = "BB_MATCH_ORDER_CANCELLED";

	public static final String TAGS_NOT_MATCHED = "BB_MATCH_ORDER_NOT_MATCHED";
	
	public static final String TAGS_MATCHED = "BB_MATCH_ORDER_MATCHED";
	
	public static final String TAGS_TRADE = "BB_TRADE";
	
}
