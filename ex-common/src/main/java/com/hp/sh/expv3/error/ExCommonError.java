package com.hp.sh.expv3.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 现金充提错误码
 * 01-01-??
 * @author wangjg
 *
 */
public class ExCommonError extends ErrorCode {

	//重复提交
	public static final ExCommonError REPEAT_ORDER = new ExCommonError(10101, "重复提交！");
	public static final ExCommonError OBJ_DONT_EXIST = new ExCommonError(10102, "对象不存在");
	
	private ExCommonError(int code, String message) {
		super(code, message);
	}

}
