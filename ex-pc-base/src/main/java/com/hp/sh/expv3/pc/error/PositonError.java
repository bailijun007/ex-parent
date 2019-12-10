package com.hp.sh.expv3.pc.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 错误码
 * 02-01-00
 * @author wangjg
 *
 */
public class PositonError extends ErrorCode {

	public static final PositonError POS_NOT_ENOUGH = new PositonError(020200, "超过了仓位!");
	
	private PositonError(int code, String message) {
		super(code, message);
	}

}
