package com.hp.sh.expv3.fund.transfer.constant;

/**
 * 划转状态
 * @author wangjg
 *
 */
public class FundTransferStatus {

	public static final int STATUS_NEW = 1; // 创建
	
	public static final int STATUS_SRC_COMPLETE = 3; // 源账号完成
	
	public static final int STATUS_TARGET_COMPLETE = 7; // 目标账号完成
	
	public static final int STATUS_SUCCESS = 15; // 成功
	
	public static final int STATUS_FAIL = 16; // 失败
	
}
