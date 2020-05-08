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

    public static final ExCommonError REQUIRE_POSITIVE_AMOUNT = new ExCommonError(10104, "金额必须是正数!");
    
    public static final ExCommonError REQUIRE_POSITIVE_PARAM = new ExCommonError(10105, "参数必须是正数!");

    public static final ExCommonError REQUIRE_BOOL = new ExCommonError(10106, "必须是布尔值:0/1!");
    
	public static final ExCommonError DATA_EXPIRED = new ExCommonError(-5014, "服务器忙！");
	
	public static final ExCommonError LOCK = new ExCommonError(-5015, "服务器忙！！");
	
	
	private ExCommonError(int code, String message) {
		super(code, message);
	}

}
