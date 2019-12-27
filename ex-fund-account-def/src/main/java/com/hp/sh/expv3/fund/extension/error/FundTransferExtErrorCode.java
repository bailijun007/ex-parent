package com.hp.sh.expv3.fund.extension.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public class FundTransferExtErrorCode extends ErrorCode {

    public static final WithdrawalAddressExtErrorCode PARAM_EMPTY = new WithdrawalAddressExtErrorCode(8500, "缺少必填参数!");




    protected FundTransferExtErrorCode(int code, String message) {
        super(code, message);
    }
}