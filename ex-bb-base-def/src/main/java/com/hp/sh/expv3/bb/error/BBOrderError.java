package com.hp.sh.expv3.bb.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * pc永续合约异常模块
 * -13001 ~ -13999 见： pc_exception
 * 订单：-13001 ~ -13099
 * @author wangjg
 *
 */
public class BBOrderError extends ErrorCode {

	//order
	public static final BBOrderError CREATED = new BBOrderError(-13001, "订单已经创建过了!");
	
	public static final BBOrderError CANCELED = new BBOrderError(-13002, "订单已经取消!");
	
	public static final BBOrderError FILLED = new BBOrderError(-13003, "订单已成交!");
	
	public static final BBOrderError NOT_ACTIVE = new BBOrderError(-13004, "订单已经已结束!");
	
	public static final BBOrderError BANKRUPT_PRICE = new BBOrderError(-13005, "平仓委托价格不能低于破产价!");
	
	public static final BBOrderError ORDER_DONT_EXIST = new BBOrderError(-13006, "订单不存在!");

	private BBOrderError(int code, String message) {
		super(code, message);
	}

}
