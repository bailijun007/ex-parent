package com.hp.sh.expv3.fund.wallet.constant;

/**
 * 交易类型
 * @author lw
 *
 */
public class TradeType {
	
	public static final int DEPOSIT = 1; 			//充值
	public static final int CONSUME = 2;			//消费
		
	public static final int WITHDRAWAL = 3;			//提现
	public static final int WITHDRAWAL_RETURN = 4;	//后台扣款
	
	public static final int ADD = 11;				//后台充值
	public static final int CUT = 12;				//后台扣款
	
	public static final int TRANSFER_IN = 13;		//资金转入
	public static final int TRANSFER_OUT = 14;		//资金转出
	
	public static final int C2C_IN = 15;			//C2C买入
	public static final int C2C_OUT = 16;			//C2C卖出

}
