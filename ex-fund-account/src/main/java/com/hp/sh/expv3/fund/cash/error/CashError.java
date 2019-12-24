package com.hp.sh.expv3.fund.cash.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 现金充提错误码
 * 
 * @author wangjg
 *
 */
public class CashError extends ErrorCode {

	//发生了违反业务逻辑的错误!
	public static final CashError BIZ_LOGIC_ERR = new CashError(010100, "见鬼了！");
	//重复提交
	public static final CashError REPEAT_ORDER = new CashError(010101, "记录有已存在！");
	
	private CashError(int code, String message) {
		super(code, message);
	}

}
