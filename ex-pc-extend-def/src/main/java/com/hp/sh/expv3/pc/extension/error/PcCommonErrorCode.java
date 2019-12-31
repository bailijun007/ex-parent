package com.hp.sh.expv3.pc.extension.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 3-00-??
 * @author BaiLiJun  on 2019/12/20
 */
public class PcCommonErrorCode extends ErrorCode {

    public static final PcCommonErrorCode PARAM_EMPTY = new PcCommonErrorCode(8600, "缺少必填参数!");

    public static final PcCommonErrorCode INIT_MARGIN_NOT_EQUAL_ZERO = new PcCommonErrorCode(8601, "初始保证金不能为0");

    public static final PcCommonErrorCode SAVE_PC_ACCOUNT_LOG_FAIL= new PcCommonErrorCode(8602, "保存账户日志失败");



    protected PcCommonErrorCode(int code, String message) {
        super(code, message);
    }
}
