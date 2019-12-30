package com.hp.sh.expv3.fund.transfer.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 现金充提错误码
 * 01-02-??
 * @author wangjg
 *
 */
public class TransferError extends ErrorCode {

	public static final TransferError ACCOUNT_TYPE = new TransferError(010200, "账户类型错误");
	
	private TransferError(int code, String message) {
		super(code, message);
	}

}
