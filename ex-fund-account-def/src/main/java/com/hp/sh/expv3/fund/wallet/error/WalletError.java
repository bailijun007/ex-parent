package com.hp.sh.expv3.fund.wallet.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 错误码
 * 01-00-??
 * @author wangjg
 *
 */
public class WalletError extends ErrorCode {

	//user
	public static final WalletError TYPE_ERROR = new WalletError(10000, "记录类型错误!");
	
	public static final WalletError INCONSISTENT_REQUESTS = new WalletError(10001, "请求不一致!");
	
	public static final WalletError NOT_ENOUGH = new WalletError(10002, "余额不足！");
	
	private WalletError(int code, String message) {
		super(code, message);
	}

}
