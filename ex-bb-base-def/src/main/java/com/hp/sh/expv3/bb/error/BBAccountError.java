package com.hp.sh.expv3.bb.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;
/**
 * 币币账户模块异常
 * -17001 ~ -17999 见： bb_exception
 * 币币账户 :-17001 ~ -17005
 * @author wangjg
 *
 */
public class BBAccountError extends ErrorCode {

	//user
	public static final BBAccountError TYPE_ERROR = new BBAccountError(-17001, "记录类型错误!");
	
	public static final BBAccountError INCONSISTENT_REQUESTS = new BBAccountError(-17002, "币币账户变更请求不一致!");
	
	public static final BBAccountError BALANCE_NOT_ENOUGH = new BBAccountError(-17003, "可用余额不足!");
	
	public static final BBAccountError FROZEN_NOT_ENOUGH = new BBAccountError(-17004, "冻结余额不足!");
	
	private BBAccountError(int code, String message) {
		super(code, message);
	}

}
