package com.hp.sh.expv3.bb.constant;

/**
 * 交易角色
 * @author wangjg
 *
 */
public class TradeRoles {

	public static final int MAKER = 1;
	
	public static final int TAKER = 0;

	public static boolean isMaker(int flag){
		return flag==MAKER;
	}
	
}
