package com.hp.sh.expv3.fund.wallet.constant;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 错误码
 * 
 * @author wangjg
 *
 */
public class WalletError extends ErrorCode {

	//user
	public static final WalletError TYPE_ERROR = new WalletError(10201, "记录类型错误!");
	public static final WalletError INCONSISTENT_REQUESTS = new WalletError(10202, "请求不一致!");
	
	private WalletError(int code, String message) {
		super(code, message);
	}

}
