package com.hp.sh.expv3.fund.extension.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 资金账户错误码
 * 01-08-??
 *
 * @author BaiLiJun  on 2019/12/14
 */
public class FundAccountExtError extends ErrorCode {

    public static final FundAccountExtError PARAM_EMPTY = new FundAccountExtError(8100, "缺少必填参数!");







    protected FundAccountExtError(int code, String message) {
        super(code, message);
    }
}
