package com.hp.sh.expv3.fund.extension.constant;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public class WithdrawalAddressExtErrorCode extends ErrorCode {
    public static final WithdrawalAddressExtErrorCode PARAM_EMPTY = new WithdrawalAddressExtErrorCode(8400, "缺少必填参数!");

    public static final WithdrawalAddressExtErrorCode DATA_EMPTY = new WithdrawalAddressExtErrorCode(8401, "没有查询到数据!");



    protected WithdrawalAddressExtErrorCode(int code, String message) {
        super(code, message);
    }
}