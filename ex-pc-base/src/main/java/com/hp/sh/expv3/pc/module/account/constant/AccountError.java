package com.hp.sh.expv3.pc.module.account.constant;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 错误码
 * 
 * @author wangjg
 *
 */
public class AccountError extends ErrorCode {

	//user
	public static final AccountError TYPE_ERROR = new AccountError(10201, "记录类型错误!");
	public static final AccountError INCONSISTENT_REQUESTS = new AccountError(10202, "请求不一致!");
	public static final AccountError BALANCE_NOT_ENOUGH = new AccountError(10203, "余额不足!");
	
	private AccountError(int code, String message) {
		super(code, message);
	}

}
