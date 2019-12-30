package com.hp.sh.expv3.fund.extension.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 01-06-??
 * @author BaiLiJun  on 2019/12/16
 */
public class AddressExtError extends ErrorCode {
    public static final AddressExtError PARAM_EMPTY = new AddressExtError(8400, "缺少必填参数!");




    protected AddressExtError(int code, String message) {
        super(code, message);
    }
}
