/**
 * 
 */
package com.hp.sh.expv3.commons.exception;

import java.util.Arrays;

import com.gitee.hupadev.base.exceptions.BizException;
import com.gitee.hupadev.base.exceptions.ErrorCode;

public class ExException extends BizException {

	private static final long serialVersionUID = 1L;

	public ExException(ErrorCode errorCode) {
		super(errorCode);
	}

	public ExException(ErrorCode errorCode, Object...errorData) {
		super(errorCode);
		super.setErrorData(errorData);
	}
	
    public String getMessage() {
        return super.getMessage()+". "+Arrays.toString(getErrorData());
    }

	@Override
	public String toString() {
		return "ExException [code=" + getCode() + ", errorData=" + Arrays.toString(getErrorData())
				+ ", message=" + getMessage() + "]";
	}

}
