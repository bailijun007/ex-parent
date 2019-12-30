package com.hp.sh.expv3.fund.extension.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 01-10-??
 * @author BaiLiJun  on 2019/12/16
 */
public class WithdrawalExtError extends ErrorCode {
    public static final WithdrawalExtError PARAM_EMPTY = new WithdrawalExtError(8200, "缺少必填参数!");




    protected WithdrawalExtError(int code, String message) {
        super(code, message);
    }
}
