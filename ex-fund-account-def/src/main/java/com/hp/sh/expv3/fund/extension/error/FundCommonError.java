package com.hp.sh.expv3.fund.extension.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 01-05-??
 * @author BaiLiJun  on 2019/12/16
 */
public class FundCommonError extends ErrorCode {
    public static final FundCommonError PARAM_EMPTY = new FundCommonError(8400, "缺少必填参数!");




    protected FundCommonError(int code, String message) {
        super(code, message);
    }
}
