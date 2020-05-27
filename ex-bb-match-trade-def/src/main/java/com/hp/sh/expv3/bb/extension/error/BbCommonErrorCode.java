package com.hp.sh.expv3.bb.extension.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * -13251 ~ -13259
 * @author BaiLiJun  on 2019/12/20
 */
public class BbCommonErrorCode extends ErrorCode {

    public static final BbCommonErrorCode PARAM_EMPTY = new BbCommonErrorCode(-13251, "缺少必填参数!");

    public static final BbCommonErrorCode INIT_MARGIN_NOT_EQUAL_ZERO = new BbCommonErrorCode(-13252, "初始保证金不能为0");

    public static final BbCommonErrorCode SAVE_PC_ACCOUNT_LOG_FAIL= new BbCommonErrorCode(-13253, "保存账户日志失败");

    public static final BbCommonErrorCode MORE_THAN_MAX_ROW= new BbCommonErrorCode(-13254, "超过最大条数");


    private BbCommonErrorCode(int code, String message) {
        super(code, message);
    }
}
