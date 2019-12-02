/**
 * 
 */
package com.hp.sh.expv3.commons.exception;

import com.gitee.hupadev.base.exceptions.BizException;
import com.gitee.hupadev.base.exceptions.ErrorCode;

public class ExException extends BizException {

	private static final long serialVersionUID = 1L;

	public ExException(ErrorCode errorCode) {
		super(errorCode);
	}

	public ExException(int code, String message) {
		super(code, message);
	}

}
