package com.hp.sh.expv3.bb.constant;

/**
 * 订单状态
 * 
 * @author wangjg
 *
 */
public class OrderStatus {

	/**
	 * 已创建未匹配
	 */
	public static final int PENDING_NEW = 1;
	
	/**
	 * 新建未成交
	 */
	public static final int NEW = 2;
	
	/**
	 * 待取消
	 */
	public static final int PENDING_CANCEL = 4;
	
	/**
	 * 已取消
	 */
	public static final int CANCELED = 8;
	
	/**
	 * 部分成交
	 */
	public static final int PARTIALLY_FILLED = 16;
	
	/**
	 * 全部成交
	 */
	public static final int FILLED = 32;
	
	/**
	 * 提交失败
	 */
	public static final int FAILED = 64;
	
	/**
	 * 已过期
	 */
	public static final int EXPIRED = 128;

}
