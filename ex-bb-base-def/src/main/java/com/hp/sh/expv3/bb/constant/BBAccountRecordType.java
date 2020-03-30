package com.hp.sh.expv3.bb.constant;

/**
 * 交易记录类型
 * @author lw
 *
 */
public class BBAccountRecordType {
	
	public static final int ADD = 1;		//收入
	
	public static final int CUT = -1;		//支出

	public static final int FROZEN = -2;	//冻结
	
	public static final int UNFROZEN = 2;	//解冻
	
	public static final int RELEASE = -3;	//释放

}
