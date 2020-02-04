package com.hp.sh.expv3.bb.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;
/**
 * pc永续合约异常模块
 * -13001 ~ -13999 见： pc_exception
 * 账号：-13201 ~ -13299
 * @author wangjg
 *
 */
public class BBAccountError extends ErrorCode {

	//user
	public static final BBAccountError TYPE_ERROR = new BBAccountError(-13201, "记录类型错误!");
	
	public static final BBAccountError INCONSISTENT_REQUESTS = new BBAccountError(-13202, "永续合约账户变更请求不一致!");
	
	public static final BBAccountError BALANCE_NOT_ENOUGH = new BBAccountError(-13203, "余额不足!");
	
	private BBAccountError(int code, String message) {
		super(code, message);
	}

}
