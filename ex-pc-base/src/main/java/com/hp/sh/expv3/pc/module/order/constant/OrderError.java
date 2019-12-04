package com.hp.sh.expv3.pc.module.order.constant;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 错误码
 * 
 * @author wangjg
 *
 */
public class OrderError extends ErrorCode {

	//user
	public static final OrderError CANCELED = new OrderError(10301, "订单已经取消!");
	public static final OrderError FILLED = new OrderError(10301, "订单已成交!");
	
	private OrderError(int code, String message) {
		super(code, message);
	}

}
