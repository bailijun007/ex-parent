package com.hp.sh.expv3.fund.extension.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 01-05-??
 * @author BaiLiJun  on 2019/12/16
 */
public class FundCommonError extends ErrorCode {
    public static final FundCommonError PARAM_EMPTY = new FundCommonError(010500, "缺少必填参数!");
    public static final DepositExtError ACCOUNT_NOT_FIND = new DepositExtError(010501, "账户不存在！");




    protected FundCommonError(int code, String message) {
        super(code, message);
    }
}
