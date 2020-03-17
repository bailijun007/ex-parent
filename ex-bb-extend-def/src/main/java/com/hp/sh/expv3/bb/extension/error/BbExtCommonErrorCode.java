package com.hp.sh.expv3.bb.extension.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * * 币币扩展异常模块
 *  * Ext：-17051 ~ -17059
 * @author BaiLiJun  on 2019/12/20
 */
public class BbExtCommonErrorCode extends ErrorCode {

    public static final BbExtCommonErrorCode PARAM_EMPTY = new BbExtCommonErrorCode(-17051, "缺少必填参数!");

    public static final BbExtCommonErrorCode ACCOUNT_DOES_NOT_EXIST = new BbExtCommonErrorCode(-17052, "账户不存在!");

    public static final BbExtCommonErrorCode MORE_THAN_MAX_ROW= new BbExtCommonErrorCode(-17053, "超过最大条数");

    public static final BbExtCommonErrorCode ACCOUNT_ALREADY_EXISTS= new BbExtCommonErrorCode(-17054, "账户已存在，请重新创建");

    private BbExtCommonErrorCode(int code, String message) {
        super(code, message);
    }
}
