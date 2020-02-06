package com.hp.sh.expv3.bb.constant;

/**
 * 
 * @author wangjg
 *
 */
public class OrderFlag {
	
	//买
	public static final int BID_BUY = 1;
	
	//卖
	public static final int BID_SELL = 0;
	
	//开仓
	public static final int ACTION_OPEN = 0;
	
	//平仓
	public static final int ACTION_CLOSE = 1;
	
	//空仓
	public static final int TYPE_SHORT = 0;
	
	//多仓
	public static final int TYPE_LONG = 1;
	
	
	//以下未用到，目前不是这么定义的
	private static final int BID_OPEN_LONG = (ACTION_OPEN<<1)|TYPE_LONG;			//开多 +
	private static final int ASK_OPEN_SHORT = (ACTION_OPEN<<1)|TYPE_SHORT;;			//开空 -
	private static final int ASK_CLOPSE_LONG = (ACTION_CLOSE<<1)|TYPE_LONG;;		//平多 -
	private static final int BID_CLOPSE_SHORT = (ACTION_CLOSE<<1)|TYPE_SHORT;;		//平空 +
	
	public static void main(String[] args) {
		System.out.println(BID_OPEN_LONG);
		System.out.println(ASK_OPEN_SHORT);
		System.out.println(ASK_CLOPSE_LONG);
		System.out.println(BID_CLOPSE_SHORT);
	}

}
