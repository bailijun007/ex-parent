package com.hp.sh.expv3.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 现金充提错误码
 * 01-01-??
 * @author wangjg
 *
 */
public class ExCommonError extends ErrorCode {

    public static final ExCommonError PARAM_EMPTY = new ExCommonError(10100, "缺少必填参数!");
    
	//重复提交
	public static final ExCommonError REPEAT_ORDER = new ExCommonError(10101, "重复提交！");
	
	public static final ExCommonError OBJ_DONT_EXIST = new ExCommonError(10102, "对象不存在");

	public static final ExCommonError UNSUPPORTED = new ExCommonError(10103, "不支持此功能");

    public static final ExCommonError REQUIRE_POSITIVE = new ExCommonError(10104, "金额必须是正数!");
	
	
	private ExCommonError(int code, String message) {
		super(code, message);
	}

}
