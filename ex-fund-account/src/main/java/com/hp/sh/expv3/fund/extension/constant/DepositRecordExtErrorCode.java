package com.hp.sh.expv3.fund.extension.constant;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public class DepositRecordExtErrorCode extends ErrorCode {

    public static final DepositRecordExtErrorCode PARAM_EMPTY = new DepositRecordExtErrorCode(8300, "缺少必填参数!");

    public static final DepositRecordExtErrorCode DATA_EMPTY = new DepositRecordExtErrorCode(8301, "没有查询到数据!");



    protected DepositRecordExtErrorCode(int code, String message) {
        super(code, message);
    }
}