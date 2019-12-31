package com.hp.sh.expv3.pc.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 错误码
 * 02-01-00
 * @author wangjg
 *
 */
public class OrderError extends ErrorCode {

	//order
	public static final OrderError CREATED = new OrderError(20100, "订单已经创建过了!");
	
	public static final OrderError CANCELED = new OrderError(20101, "订单已经取消!");
	
	public static final OrderError CANCELED_NUM_ERR = new OrderError(20102, "取消数量与剩余数量不符!");

	public static final OrderError FILLED = new OrderError(20103, "订单已成交!");
	
	public static final OrderError NOT_ACTIVE = new OrderError(20104, "订单已经已结束!");
	
	public static final OrderError BANKRUPT_PRICE = new OrderError(20105, "平仓委托价格不能低于破产价!");

	public static final OrderError POS_LIQ = new OrderError(20106, "仓位强平中!");
	
	public static final OrderError POS_LIQED = new OrderError(20107, "仓位已强平!");
	
	private OrderError(int code, String message) {
		super(code, message);
	}

}
