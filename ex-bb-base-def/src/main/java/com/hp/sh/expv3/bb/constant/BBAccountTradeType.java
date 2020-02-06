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

	public static final int ORDER_OPEN_LONG = 3;		//-开多（押金）
	public static final int ORDER_OPEN_SHORT = 4;		//-开空（押金）
	
	public static final int ORDER_CLOSE_LONG = 5;		//+平多 （保证金+收益-手续费）
	public static final int ORDER_CLOSE_SHORT = 6;		//+平空 （保证金+收益-手续费）
	
	public static final int ORDER_CANCEL = 7;			//+撤单（退回押金）

	public static final int ADD_TO_MARGIN = 8;			//-手动追加保证金
	public static final int REDUCE_MARGIN = 9;			//+减少保证金
	public static final int AUTO_ADD_MARGIN = 10;		//-自动追加保证金
	public static final int LEVERAGE_ADD_MARGIN = 11;	//-调低杠杆追加保证金
	
	public static final int RETURN_FEE_DIFF = 12;		//+退回多收的手续费 taker fee - maker fee

}
