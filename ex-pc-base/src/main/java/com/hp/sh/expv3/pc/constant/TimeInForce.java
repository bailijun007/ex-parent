package com.hp.sh.expv3.pc.constant;

/**
 * 委托有效时间
 * @author wangjg
 *
 */
public class TimeInForce {

	/**
	 * 一直有效
	 */
	public static final int GOOD_TILL_CANCEL = 0;
	
	/**
	 * 只做Maker
	 */
	public static final int MAKER_ONLY = 0;
	
	/**
	 * 立即 成交或取消，能成交多少是多少
	 */
	public static final int IMMEDIATE_OR_CANCEL = 0;
	
	/**
	 * 全部成交或取消
	 */
	public static final int FILL_OR_KILL = 0;
	
}
