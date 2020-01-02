package com.hp.sh.expv3.fund.c2c.component;

import com.hp.sh.expv3.fund.c2c.entity.C2cOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Component
public class PLPayService {
    @Autowired
    private PLpayClient pLpayClient;

    /**
     * @param userId      用户id
     * @param ratio       买入估价
     * @param srcCurrency 支付币种
     * @param tarCurrency 兑换币种
     * @param fabiAmt     法定货币总金额
     * @param tarVolume   兑换成资产数量
     * @return
     */
    // TODO 老王，入金回调如何确保幂等（高并发情况下，如何确认入金状态与入金账户的修改）
    public String rujin(long userId, BigDecimal ratio, String srcCurrency, String tarCurrency, BigDecimal fabiAmt, BigDecimal tarVolume) {
        //买家姓名
        String customerId = userId + "";
        //生成订单号
        String orderNo = getOrderNo();
        //订单币种
        String orderCurrency = srcCurrency;
        //订单金额
        String orderAmount = fabiAmt.stripTrailingZeros().toPlainString();//
        String receiveUrl = getReceiveUrl();//
        String pickupUrl = getPickupUrl(); //
        String sign = pLpayClient.getSign(pickupUrl, receiveUrl, orderNo, orderAmount, orderCurrency, customerId);

        String response = pLpayClient.postOrder(customerId, orderNo, orderCurrency, orderAmount, receiveUrl, pickupUrl, sign);


        return "第三方入金URL";
    }

    //生成并返回订单编号
    String getOrderNo() {
        int random = (int) (Math.random() * 100);
        Instant instant = Instant.now();
        long timestamp = instant.toEpochMilli();
        String prefix = "c2c";
        String sn = prefix + timestamp + random;

        return sn;
    }


    String getOrderAmout() {
        return "";
    }

    String getReceiveUrl() {
        return "";
    }

    String getPickupUrl() {
        return "";
    }




    String getShopNo() {
        return "";
    }



}
