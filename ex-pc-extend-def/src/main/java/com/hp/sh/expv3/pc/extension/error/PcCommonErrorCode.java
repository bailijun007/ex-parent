package com.hp.sh.expv3.pc.extension.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * -13251 ~ -13259
 * @author BaiLiJun  on 2019/12/20
 */
public class PcCommonErrorCode extends ErrorCode {

    public static final PcCommonErrorCode PARAM_EMPTY = new PcCommonErrorCode(-13251, "缺少必填参数!");

    public static final PcCommonErrorCode INIT_MARGIN_NOT_EQUAL_ZERO = new PcCommonErrorCode(-13252, "初始保证金不能为0");

    public static final PcCommonErrorCode SAVE_PC_ACCOUNT_LOG_FAIL= new PcCommonErrorCode(-13253, "保存账户日志失败");

    public static final PcCommonErrorCode MORE_THAN_MAX_ROW= new PcCommonErrorCode(-13254, "超过最大条数");


    private PcCommonErrorCode(int code, String message) {
        super(code, message);
    }
}
