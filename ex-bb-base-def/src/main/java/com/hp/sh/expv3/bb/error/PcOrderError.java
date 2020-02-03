package com.hp.sh.expv3.bb.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * pc永续合约异常模块
 * -13001 ~ -13999 见： pc_exception
 * 订单：-13001 ~ -13099
 * @author wangjg
 *
 */
public class PcOrderError extends ErrorCode {

	//order
	public static final PcOrderError CREATED = new PcOrderError(-13001, "订单已经创建过了!");
	
	public static final PcOrderError CANCELED = new PcOrderError(-13002, "订单已经取消!");
	
	public static final PcOrderError FILLED = new PcOrderError(-13003, "订单已成交!");
	
	public static final PcOrderError NOT_ACTIVE = new PcOrderError(-13004, "订单已经已结束!");
	
	public static final PcOrderError BANKRUPT_PRICE = new PcOrderError(-13005, "平仓委托价格不能低于破产价!");

	private PcOrderError(int code, String message) {
		super(code, message);
	}

}
