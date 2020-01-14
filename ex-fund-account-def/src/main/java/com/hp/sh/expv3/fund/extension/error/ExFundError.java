package com.hp.sh.expv3.fund.extension.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 1-05-??
 * @author BaiLiJun  on 2019/12/16
 */
public class ExFundError extends ErrorCode {
    public static final ExFundError PARAM_EMPTY = new ExFundError(10500, "缺少必填参数!");
    public static final ExFundError ACCOUNT_NOT_FIND = new ExFundError(10501, "账户不存在！");
    public static final ExFundError SEND_REQUEST_TO_C2C_SERVICE_FAIL = new ExFundError(10502, "订单发送请求到第三方支付发生错误");
    public static final ExFundError ORDER_CALLBACK_NOTIFY_FIND_SIGN_ERROR = new ExFundError(10503, "订单成功回调通知签名不对");
    public static final ExFundError ORDER_CALLBACK_NOTIFY_FAIL = new ExFundError(10504, "订单回调通知失败");
    public static final ExFundError ORDER_NOT_SUFFICIENT_FUNDS = new ExFundError(10505, "余额不足");
    public static final ExFundError ORDER_NOT_FIND = new ExFundError(10506, "订单不存在");
    public static final ExFundError UPDATE_C2C_ORDER_FAIL = new ExFundError(10507, "更新c2c订单失败");




    private ExFundError(int code, String message) {
        super(code, message);
    }
}
