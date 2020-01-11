package com.hp.sh.expv3.pc.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 错误码
 * 02-01-00
 * @author wangjg
 *
 */
public class PcOrderError extends ErrorCode {

	//order
	public static final PcOrderError CREATED = new PcOrderError(20100, "订单已经创建过了!");
	
	public static final PcOrderError CANCELED = new PcOrderError(20101, "订单已经取消!");
	
	public static final PcOrderError FILLED = new PcOrderError(20102, "订单已成交!");
	
	public static final PcOrderError NOT_ACTIVE = new PcOrderError(20103, "订单已经已结束!");
	
	public static final PcOrderError BANKRUPT_PRICE = new PcOrderError(20104, "平仓委托价格不能低于破产价!");

	private PcOrderError(int code, String message) {
		super(code, message);
	}

}
