package com.hp.sh.expv3.bb.constant;

/**
 * 交易类型
 * @author lw
 *
 */
public class BBAccountTradeType {
	
	public static final int ORDER_BUY = 1;				//买入(押金)
	
	public static final int ORDER_SELL = 2;				//卖出(押金)
	
	public static final int ACCOUNT_FUND_TO_BB = 3;		//从资金账户转入
	
	public static final int ACCOUNT_BB_TO_FUND = 4;		//转出至资金账户
	
	public static final int ACCOUNT_PC_TO_BB = 5;		//从永续合约转入,
	
	public static final int ACCOUNT_BB_TO_PC = 6;		//转出至永续合约
	
	public static final int ORDER_CANCEL = 7;			//+撤单（退回押金）
	
	public static final int RETURN_ORDER_MARGIN = 8;	//+退回多收的手续费 taker fee - maker fee
	
	public static final int TRADE_BUY_IN = 9;			//买入
	
	public static final int TRADE_SELL_INCOME = 10;		//卖出收入

}
