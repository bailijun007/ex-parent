package com.hp.sh.expv3.fund.cash.constant;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 现金充提错误码
 * 
 * @author wangjg
 *
 */
public class CashError extends ErrorCode {

	//发生了违反业务逻辑的错误!
	public static final CashError BIZ_LOGIC_ERR = new CashError(11000, "见鬼了！");
	
	private CashError(int code, String message) {
		super(code, message);
	}

}
