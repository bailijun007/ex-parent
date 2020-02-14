package com.hp.sh.expv3.bb.extension.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * * 币币异常模块
 *  * -13001 ~ -13999 见： bb_exception
 *  * Ext：-13301 ~ -13399
 * @author BaiLiJun  on 2019/12/20
 */
public class BbExtCommonErrorCode extends ErrorCode {

    public static final BbExtCommonErrorCode PARAM_EMPTY = new BbExtCommonErrorCode(-13301, "缺少必填参数!");

    public static final BbExtCommonErrorCode ACCOUNT_DOES_NOT_EXIST = new BbExtCommonErrorCode(-13302, "账户不存在!");


    private BbExtCommonErrorCode(int code, String message) {
        super(code, message);
    }
}
