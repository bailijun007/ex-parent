package com.hp.sh.expv3.fund.c2c.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PLpayClient {
    @Value("${plpay.server.host}")
    private String apiHost;

    @Value("${plpay.server.shopNo}")
    private String shopNo;

    @Value("${plpay.server.signType}")
    private String signType;

    @Value("${plpay.server.md5Key}")
    private String md5Key;


    public String postOrder(String customerId, String orderNo, String orderCurrency, String orderAmount, String receiveUrl, String pickupUrl,  String sign) {

        return null;
    }

    public String getSign(String pickupUrl, String receiveUrl,  String orderNo, String orderAmount, String orderCurrency, String customerId) {
        return "";
    }

}
