package com.hp.sh.expv3.pc.constant;

/**
 * 交易类型
 * @author lw
 *
 */
public class PcAccountTradeType {
	
	public static final int FUND_TO_PC = 1;		//+资金转入
	
	public static final int PC_TO_FUND = 2;		//-资金转出

	public static final int ORDER_OPEN = 3;		//-开仓
	
	public static final int ORDER_CANCEL = 4;	//+撤单

	public static final int ORDER_CLOSE = 5;	//+平仓	//保证金+收益-手续费
	
	public static final int ADD_TO_MARGIN = 6;		//-追加保证金
	
	public static final int ADD_MARGIN2 = 7;	//-修改杠杆追加保证金
	
	public static final int RETURN_FEE_DIFF = 8;//+退回多收的手续费 taker fee - maker fee

}
