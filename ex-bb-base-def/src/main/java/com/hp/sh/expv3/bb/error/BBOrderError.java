package com.hp.sh.expv3.bb.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 币币订单
 * -17001 ~ -17999 见： bb_exception
 * 手续费 :-17011 ~ -17019
 * @author wangjg
 *
 */
public class BBOrderError extends ErrorCode {

	//order
	public static final BBOrderError CREATED = new BBOrderError(-17011, "订单已经创建过了!");
	
	public static final BBOrderError CANCELED = new BBOrderError(-17012, "订单已经取消!");
	
	public static final BBOrderError FILLED = new BBOrderError(-17013, "订单已成交!");
	
	public static final BBOrderError NOT_ACTIVE = new BBOrderError(-17014, "订单已经已结束!");
	
	public static final BBOrderError BANKRUPT_PRICE = new BBOrderError(-17015, "平仓委托价格不能低于破产价!");
	
	public static final BBOrderError ORDER_DONT_EXIST = new BBOrderError(-17016, "订单不存在!");

	private BBOrderError(int code, String message) {
		super(code, message);
	}

}
