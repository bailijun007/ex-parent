package com.hp.sh.expv3.fund.extension.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public class DepositRecordExtErrorCode extends ErrorCode {

    public static final DepositRecordExtErrorCode PARAM_EMPTY = new DepositRecordExtErrorCode(8300, "缺少必填参数!");

    public static final DepositRecordExtErrorCode ACCOUNT_NOT_FIND = new DepositRecordExtErrorCode(8301, "账户不存在！");


    protected DepositRecordExtErrorCode(int code, String message) {
        super(code, message);
    }
}
