package com.hp.sh.expv3.fund.transfer.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;


/**
 * 账户模块异常
 * -11001 ~ -11999 见： account_exception
 * 转账 :-11051 ~ -11060
 *
 */
public class TransferError extends ErrorCode {

	public static final TransferError SAME_ACCOUNT_TYPE = new TransferError(-11051, "账户类型不能相同");
	
	private TransferError(int code, String message) {
		super(code, message);
	}

}
  