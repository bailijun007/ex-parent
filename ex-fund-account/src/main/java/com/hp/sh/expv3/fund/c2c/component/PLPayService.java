package com.hp.sh.expv3.fund.c2c.component;

import com.gitee.hupadev.base.exceptions.CommonError;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.c2c.entity.C2cOrder;
import com.hp.sh.expv3.fund.c2c.entity.NotifyParam;
import com.hp.sh.expv3.fund.c2c.service.BuyService;
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

    @Autowired
    private BuyService buyService;

    /**
     * @param userId      用户id
     * @param ratio       USD/CNY 汇率 例如： USD/CNY = 7.0298
     * @param srcCurrency 支付币种 例如：
     * @param tarCurrency 兑换币种
     * @param fabiAmt     法定货币总金额
     * @param tarVolume   兑换成资产数量
     *                    TODO 老王，入金回调如何确保幂等（高并发情况下，如何确认入金状态与入金账户的修改）
     * @return 返回转发的url地址，调用方需要转发到该地址获取数据
     */
    public String rujin(long userId, BigDecimal ratio, String srcCurrency, String tarCurrency, BigDecimal fabiAmt, BigDecimal tarVolume) {
        //生成订单号
        String orderNo = getOrderNo(userId);
        //订单币种
        String orderCurrency = srcCurrency;
        //订单金额
        String orderAmount = fabiAmt.stripTrailingZeros().toPlainString();
        String receiveUrl = getReceiveUrl();
        String pickupUrl = getPickupUrl();
        //获取加密后的签名
        String sign = pLpayClient.getSign(pickupUrl, receiveUrl, orderNo, orderAmount, orderCurrency);
        //转发请求到第三方支付，并返回支付路径
        String url = pLpayClient.sendRequestUrl(orderNo, orderCurrency, orderAmount, receiveUrl, pickupUrl, sign);


        //增加一条c2c订单记录
        C2cOrder c2cOrder=new C2cOrder();
        c2cOrder.setSn(orderNo);
        c2cOrder.setPayCurrency(srcCurrency);
        c2cOrder.setExchangeCurrency(tarCurrency);
        c2cOrder.setPrice(ratio);
        c2cOrder.setType(1);
        c2cOrder.setPayStatus(0);
        c2cOrder.setPayStatusDesc("c2c充值");
        c2cOrder.setPayTime(Instant.now().toEpochMilli());
        c2cOrder.setPayFinishTime(Instant.now().toEpochMilli());
        c2cOrder.setSynchStatus(0);
        c2cOrder.setApprovalStatus(0);
        c2cOrder.setUserId(userId);
        c2cOrder.setCreated(Instant.now().toEpochMilli());
        c2cOrder.setModified(Instant.now().toEpochMilli());
        buyService.saveC2cOrder(c2cOrder);

        return url;
    }


    //生成并返回订单编号
    private String getOrderNo(long userId) {
        String s = "";
        for (int i = 0; i < 4; i++) {
            int random = (int) (Math.random() * 10);
            s += random;
        }
        Instant instant = Instant.now();
        long timestamp = instant.toEpochMilli();
        String prefix = "c2c";
        String sn = prefix + timestamp + s+"-"+userId;

        return sn;
    }


    private String getOrderAmout() {
        return "";
    }

    //通知回调地址
    private String getReceiveUrl() {
        return baseUrl + "api/callback/c2c/deposit/notify";
    }

    //交易完成跳转URL
    private String getPickupUrl() {
        return baseUrl+"api/callback/c2c/deposit/tradeSuccessSkip";
    }


    private String getShopNo() {
        return "";
    }


}
