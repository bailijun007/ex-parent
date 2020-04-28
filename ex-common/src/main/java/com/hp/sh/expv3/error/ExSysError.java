package com.hp.sh.expv3.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 现金充提错误码
 * 01-01-??
 * @author wangjg
 *
 */
public class ExSysError extends ErrorCode {

	//发生了违反业务逻辑的错误!
	public static final ExSysError BIZ_LOGIC_ERR = new ExSysError(11100, "见鬼了！");
	

	//数据更新失败,数据过期
	public static final ExSysError UPDATED_ERR = new ExSysError(11101, "数据更新失败，可能是数据过期！");
	
	private ExSysError(int code, String message) {
		super(code, message);
	}

}
