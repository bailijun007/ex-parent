package com.hp.sh.expv3.fund.extension.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 1-05-??
 * @author BaiLiJun  on 2019/12/16
 */
public class FundCommonError extends ErrorCode {
    public static final FundCommonError PARAM_EMPTY = new FundCommonError(10500, "缺少必填参数!");
    public static final FundCommonError ACCOUNT_NOT_FIND = new FundCommonError(10501, "账户不存在！");
    public static final FundCommonError SEND_REQUEST_TO_C2C_SERVICE_FAIL = new FundCommonError(10502, "订单发送请求到第三方支付发生错误");
    public static final FundCommonError ORDER_CALLBACK_NOTIFY_FIND_SIGN_ERROR = new FundCommonError(10503, "订单成功回调通知签名不对");
    public static final FundCommonError ORDER_CALLBACK_NOTIFY_FAIL = new FundCommonError(10504, "订单回调通知失败");




    private FundCommonError(int code, String message) {
        super(code, message);
    }
}
