package com.hp.sh.expv3.commons.exception;

import com.gitee.hupadev.base.exceptions.ErrorCode;
import com.gitee.hupadev.base.exceptions.SysException;

public class ExSysException extends SysException{

	private static final long serialVersionUID = 1L;

	public ExSysException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ExSysException(ErrorCode errorCode) {
		super(errorCode);
		// TODO Auto-generated constructor stub
	}

	public ExSysException(int code, String message, Throwable cause) {
		super(code, message, cause);
		// TODO Auto-generated constructor stub
	}

	public ExSysException(int code, String message) {
		super(code, message);
		// TODO Auto-generated constructor stub
	}

}
