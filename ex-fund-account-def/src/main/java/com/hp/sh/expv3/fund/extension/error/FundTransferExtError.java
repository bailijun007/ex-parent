package com.hp.sh.expv3.fund.extension.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 01-09-??
 * @author BaiLiJun  on 2019/12/16
 */
public class FundTransferExtError extends ErrorCode {

    public static final AddressExtError PARAM_EMPTY = new AddressExtError(8500, "缺少必填参数!");




    protected FundTransferExtError(int code, String message) {
        super(code, message);
    }
}
