package com.hp.sh.expv3.bb.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;
/**
 * 币币异常模块
 * -13001 ~ -13999 见： bb_exception
 * 账号：-13201 ~ -13299
 * @author wangjg
 *
 */
public class BBCollectorAccountError extends ErrorCode {

	//user
	public static final BBCollectorAccountError TYPE_ERROR = new BBCollectorAccountError(-13207, "记录类型错误!");
	
	public static final BBCollectorAccountError INCONSISTENT_REQUESTS = new BBCollectorAccountError(-13208, "手续费账户变更请求不一致!");
	
	public static final BBCollectorAccountError BALANCE_NOT_ENOUGH = new BBCollectorAccountError(-13209, "余额不足!");
	
	private BBCollectorAccountError(int code, String message) {
		super(code, message);
	}

}
