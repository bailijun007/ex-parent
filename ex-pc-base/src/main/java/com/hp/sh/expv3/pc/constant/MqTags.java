package com.hp.sh.expv3.pc.constant;

public class MqTags {
	
	//send
	public static final String TAGS_ORDER_PENDING_NEW = "PC_ORDER_PENDING_NEW";

	public static final String TAGS_ORDER_PENDING_CANCEL = "PC_ORDER_PENDING_CANCEL";

	public static final String TAGS_BOOK_RESET = "PC_BOOK_RESET";
	
	public static final String TAGS_POS_LIQ_LOCKED = "PC_POS_LIQ_LOCKED";
	
	public static final String TAGS_ORDER_REBASE = "PC_ORDER_REBASE";

	//consumer
	public static final String TAGS_CANCELLED = "PC_MATCH_ORDER_CANCELLED";

	public static final String TAGS_NOT_MATCHED = "PC_MATCH_ORDER_NOT_MATCHED";
	
	public static final String TAGS_MATCHED = "PC_MATCH_ORDER_MATCHED";
	
	public static final String TAGS_TRADE = "PC_TRADE";
	
	public static final String TAGS_ORDER_ALL_CANCELLED = "PC_MATCH_SAME_SIDE_CLOSE_ORDER_ALL_CANCELLED";

}
