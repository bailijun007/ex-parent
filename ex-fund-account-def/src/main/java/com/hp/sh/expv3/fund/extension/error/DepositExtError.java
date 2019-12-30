package com.hp.sh.expv3.fund.extension.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 01-07-??
 * @author BaiLiJun  on 2019/12/16
 */
public class DepositExtError extends ErrorCode {

    public static final DepositExtError PARAM_EMPTY = new DepositExtError(8300, "缺少必填参数!");

    public static final DepositExtError ACCOUNT_NOT_FIND = new DepositExtError(8301, "账户不存在！");


    protected DepositExtError(int code, String message) {
        super(code, message);
    }
}
