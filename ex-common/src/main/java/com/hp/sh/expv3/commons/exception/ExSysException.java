package com.hp.sh.expv3.commons.exception;

import com.gitee.hupadev.base.exceptions.ErrorCode;
import com.gitee.hupadev.base.exceptions.SysException;

public class ExSysException extends SysException{

	private static final long serialVersionUID = 1L;

	public ExSysException() {
		super();
	}

	public ExSysException(ErrorCode errorCode) {
		super(errorCode);
	}

	public ExSysException(ErrorCode errorCode, Throwable cause) {
		super(errorCode.getCode(), errorCode.getMessage(), cause);
	}
	
	public ExSysException(ErrorCode errorCode, Object...errorData) {
		this(errorCode);
		super.setErrorData(errorData);
	}

}
