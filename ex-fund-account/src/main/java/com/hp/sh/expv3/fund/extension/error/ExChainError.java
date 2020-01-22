package com.hp.sh.expv3.fund.extension.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 1-05-??
 *
 */
public class ExChainError extends ErrorCode {
    public static final ExChainError SIGN_ERROR = new ExChainError(10601, "签名错误!");

    private ExChainError(int code, String message) {
        super(code, message);
    }
}
