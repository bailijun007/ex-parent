package com.hp.sh.expv3.pc.constant;

public class LogType {

	public static final int TYPE_TRAD_OPEN_LONG = 1;				//成交开多
	public static final int TYPE_TRAD_OPEN_SHORT = 2;				//成交开空
	public static final int TYPE_TRAD_CLOSE_LONG = 3;				//成交平多
	public static final int TYPE_TRAD_CLOSE_SHORT = 4;				//成交平空
	
	public static final int TYPE_ACCOUNT_FUND_TO_PC = 5;			//转入
	public static final int TYPE_ACCOUNT_PC_TO_FUND = 6;			//转出
	
	public static final int TYPE_ACCOUNT_ADD_TO_MARGIN = 7;			//-手动追加保证金
	public static final int TYPE_ACCOUNT_REDUCE_MARGIN = 8;			//+减少保证金
	public static final int TYPE_ACCOUNT_AUTO_ADD_MARGIN = 9;		//-自动追加保证金
	public static final int TYPE_ACCOUNT_LEVERAGE_ADD_MARGIN = 10;	//-调低杠杆追加保证金
	
	public static final int TYPE_LIQ_LONG = 11;						//强平平多
	public static final int TYPE_LIQ_SHORT = 12;					//强平平空
	
}
