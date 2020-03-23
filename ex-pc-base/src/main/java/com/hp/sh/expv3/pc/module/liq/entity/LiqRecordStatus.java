package com.hp.sh.expv3.pc.module.liq.entity;

/**
 * 强平状态
 * @author wangjg
 *
 */
public class LiqRecordStatus {

	public static final int NEW = 0;
	
	//破产价委托
	public static final int BANKRUPT_ORDER = 1;

	public static final int BANKRUPT_ORDER_CANCELLED = 2;
	
	//破产价成交
	public static final int BANKRUPT_ORDER_TRADE = 2;
	
	//结束
	public static final int FINISHED = 6;

}
