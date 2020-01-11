package com.hp.sh.expv3.fund.wallet.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 账户模块异常
 * -11001 ~ -11999 见： account_exception
 * @author wangjg
 * 钱包 :-11001 ~ -11050 
 */
public class WalletError extends ErrorCode {

	//user
	public static final WalletError TYPE_ERROR = new WalletError(-11001, "记录类型错误!");
	
	public static final WalletError INCONSISTENT_REQUESTS = new WalletError(-11002, "请求不一致!");
	
	public static final WalletError NOT_ENOUGH = new WalletError(-11003, "余额不足！");
	
	private WalletError(int code, String message) {
		super(code, message);
	}

}
