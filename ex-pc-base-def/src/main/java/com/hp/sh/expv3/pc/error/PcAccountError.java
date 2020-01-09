package com.hp.sh.expv3.pc.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 错误码
 * 02-00-00
 * @author wangjg
 *
 */
public class PcAccountError extends ErrorCode {

	//user
	public static final PcAccountError TYPE_ERROR = new PcAccountError(20000, "记录类型错误!");
	
	public static final PcAccountError INCONSISTENT_REQUESTS = new PcAccountError(20001, "请求不一致!");
	
	public static final PcAccountError BALANCE_NOT_ENOUGH = new PcAccountError(20002, "余额不足!");
	
	private PcAccountError(int code, String message) {
		super(code, message);
	}

}
