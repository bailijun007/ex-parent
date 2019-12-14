package com.hp.sh.expv3.fund.extension.constant;

import com.gitee.hupadev.base.exceptions.ErrorCode;
import com.hp.sh.expv3.fund.wallet.constant.WalletError;

/**
 * 资金账户错误码
 *
 * @author BaiLiJun  on 2019/12/14
 */
public class CapitalAccountErrorCode extends ErrorCode {

    public static final CapitalAccountErrorCode PARAM_EMPTY = new CapitalAccountErrorCode(9000, "缺少必填参数!");






    protected CapitalAccountErrorCode(int code, String message) {
        super(code, message);
    }
}
