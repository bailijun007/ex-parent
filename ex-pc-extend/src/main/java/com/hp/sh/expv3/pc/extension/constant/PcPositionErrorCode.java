package com.hp.sh.expv3.pc.extension.constant;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * @author BaiLiJun  on 2019/12/20
 */
public class PcPositionErrorCode  extends ErrorCode {

    public static final PcPositionErrorCode PARAM_EMPTY = new PcPositionErrorCode(8600, "缺少必填参数!");

    public static final PcPositionErrorCode INIT_MARGIN_NOT_EQUAL_ZERO = new PcPositionErrorCode(8601, "初始保证金不能为0");


    protected PcPositionErrorCode(int code, String message) {
        super(code, message);
    }
}
