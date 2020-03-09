package com.hp.sh.expv3.bb.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;
/**
 * 币币账户模块异常
 * -17001 ~ -17999 见： bb_exception
 * 手续费 :-17006 ~ -17009
 * @author wangjg
 *
 */
public class BBCollectorAccountError extends ErrorCode {

	//user
	public static final BBCollectorAccountError TYPE_ERROR = new BBCollectorAccountError(-17006, "记录类型错误!");
	
	public static final BBCollectorAccountError INCONSISTENT_REQUESTS = new BBCollectorAccountError(-17007, "手续费账户变更请求不一致!");

	public static final BBCollectorAccountError BALANCE_NOT_ENOUGH = new BBCollectorAccountError(-17008, "余额不足!");
	
	public static final BBCollectorAccountError ZERO_AMOUNT = new BBCollectorAccountError(-17009, "账变金额必须大于零!");
	
	private BBCollectorAccountError(int code, String message) {
		super(code, message);
	}

}
