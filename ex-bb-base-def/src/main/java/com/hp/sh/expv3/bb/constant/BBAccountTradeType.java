package com.hp.sh.expv3.bb.constant;

/**
 * 交易类型
 * @author lw
 *
 */
public class BBAccountTradeType {
	
	public static final int FUND_TO_BB = 1;				//+资金转入
	
	public static final int BB_TO_FUND = 2;				//-资金转出
	
	public static final int ORDER = 3;					//-开多（押金）
	public static final int INCOME = 4;					//-开多（押金）
	
	public static final int ORDER_CANCEL = 7;			//+撤单（退回押金）
	
	public static final int RETURN_FEE_DIFF = 12;		//+退回多收的手续费 taker fee - maker fee

}
