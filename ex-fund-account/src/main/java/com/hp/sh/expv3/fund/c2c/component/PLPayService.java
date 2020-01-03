package com.hp.sh.expv3.fund.c2c.component;

import com.gitee.hupadev.base.exceptions.CommonError;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.c2c.entity.NotifyParam;
import com.hp.sh.expv3.fund.extension.error.FundCommonError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Component
public class PLPayService {
    @Autowired
    private PLpayClient pLpayClient;

    @Value("${expv3.base.url}")
    private String baseUrl;

    /**
     * @param userId      用户id
     * @param ratio       买入估价
     * @param srcCurrency 支付币种
     * @param tarCurrency 兑换币种
     * @param fabiAmt     法定货币总金额
     * @param tarVolume   兑换成资产数量
     *                    TODO 老王，入金回调如何确保幂等（高并发情况下，如何确认入金状态与入金账户的修改）
     * @return 返回转发的url地址，调用方需要转发到该地址获取数据
     */
    public String rujin(long userId, BigDecimal ratio, String srcCurrency, String tarCurrency, BigDecimal fabiAmt, BigDecimal tarVolume) {
        //买家姓名
        String customerId = userId + "";
        //生成订单号
        String orderNo = getOrderNo();
        //订单币种
        String orderCurrency = tarCurrency;
        //订单金额
        String orderAmount = fabiAmt.stripTrailingZeros().toPlainString();
        String receiveUrl = getReceiveUrl();
        String pickupUrl = getPickupUrl();
        //获取加密后的签名
        String sign = pLpayClient.getSign(pickupUrl, receiveUrl, orderNo, orderAmount, orderCurrency, customerId);
        //检查发送请求到第三方支付的url是否 返回code是200
        Boolean b = pLpayClient.checkSendUrl(customerId, orderNo, orderCurrency, orderAmount, receiveUrl, pickupUrl, sign);
        if (!b) {
        //订单发送请求到第三方支付发生错误
            throw new ExException(FundCommonError.SEND_REQUEST_TO_C2C_SERVICE_FAIL);
        }

        return "success";
    }


    //生成并返回订单编号
    private String getOrderNo() {
        int random = (int) (Math.random() * 100);
        Instant instant = Instant.now();
        long timestamp = instant.toEpochMilli();
        String prefix = "c2c";
        String sn = prefix + timestamp + random;

        return sn;
    }


    private String getOrderAmout() {
        return "";
    }

    //通知回调地址
    private String getReceiveUrl() {
        return baseUrl + "/api/callback/c2c";
    }

    //交易完成跳转URL
    private String getPickupUrl() {
        return baseUrl;
    }


    private String getShopNo() {
        return "";
    }


}
