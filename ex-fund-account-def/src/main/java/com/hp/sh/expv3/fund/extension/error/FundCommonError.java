package com.hp.sh.expv3.fund.extension.error;

import com.gitee.hupadev.base.exceptions.ErrorCode;

/**
 * 1-05-??
 * @author BaiLiJun  on 2019/12/16
 */
public class FundCommonError extends ErrorCode {
    public static final FundCommonError PARAM_EMPTY = new FundCommonError(10500, "缺少必填参数!");
    public static final DepositExtError ACCOUNT_NOT_FIND = new DepositExtError(10501, "账户不存在！");
    public static final DepositExtError SEND_REQUEST_TO_C2C_SERVICE_FAIL = new DepositExtError(10502, "订单发送请求到第三方支付发生错误");
    public static final DepositExtError ORDER_CALLBACK_NOTIFY_FIND_SIGN_ERROR = new DepositExtError(10502, "订单成功回调通知签名不对");




    protected FundCommonError(int code, String message) {
        super(code, message);
    }
}
