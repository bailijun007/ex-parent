package com.hp.sh.expv3.fund.extension.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 资金账户错误码
 *
 * @author BaiLiJun  on 2019/12/14
 */
public class FundAccountExtErrorCode extends ErrorCode {

    public static final FundAccountExtErrorCode PARAM_EMPTY = new FundAccountExtErrorCode(8100, "缺少必填参数!");







    protected FundAccountExtErrorCode(int code, String message) {
        super(code, message);
    }
}
