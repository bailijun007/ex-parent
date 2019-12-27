package com.hp.sh.expv3.fund.extension.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public class WithdrawalRecordExtErrorCode extends ErrorCode {
    public static final WithdrawalRecordExtErrorCode PARAM_EMPTY = new WithdrawalRecordExtErrorCode(8200, "缺少必填参数!");




    protected WithdrawalRecordExtErrorCode(int code, String message) {
        super(code, message);
    }
}