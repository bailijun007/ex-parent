package com.hp.sh.expv3.fund.c2c.component;

import com.hp.sh.expv3.fund.c2c.constants.C2cConst;
import com.hp.sh.expv3.fund.c2c.entity.C2cOrder;
import com.hp.sh.expv3.fund.c2c.service.BuyService;
import com.hp.sh.expv3.fund.c2c.util.GenerateOrderNumUtils;
import com.hp.sh.expv3.fund.constant.ApprovalStatus;
import com.hp.sh.expv3.utils.math.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Component
public class PLPayService {

    public static final Logger logger= LoggerFactory.getLogger(PLPayService.class);

    @Autowired
    private PLpayClient pLpayClient;


    @Value("${plpay.server.c2c_fee_ratio}")
    private String c2cFeeRatio;

    @Autowired
    private BuyService buyService;

    /**
     * @param userId      用户id
     * @param ratio       USD/CNY 汇率 例如： USD/CNY = 7.0298
     * @param srcCurrency 支付币种 例如：CNY
     * @param tarCurrency 兑换币种:USDT
     * @param fabiAmt     法定货币总金额 100
     * @param tarVolume   兑换成资产数量：14.5
     * @return 返回转发的url地址，调用方需要转发到该地址获取数据
     */
    public String rujin(long userId, BigDecimal ratio, String srcCurrency, String tarCurrency, BigDecimal tarVolume, BigDecimal fabiAmt,String receiveUrl,String pickupUrl) {

        logger.info("userId={},ratio={},srcCurrency={},tarCurrency={},tarVolume={},fabiAmt={},receiveUrl={},pickupUrl={}",userId,ratio,srcCurrency,tarCurrency,tarVolume,fabiAmt,receiveUrl,pickupUrl);

        //生成订单号
        String orderNo = GenerateOrderNumUtils.getOrderNo();
        //订单币种
        String orderCurrency =srcCurrency;
//        if(srcCurrency.equals("CNY")){
//             orderCurrency = "CNY";
//        }

        //订单金额
//        BigDecimal orderAmount = ratio.multiply(tarVolume).setScale(2, RoundingMode.UP);
        BigDecimal orderAmount = ratio.multiply(tarVolume);

        //获取加密后的签名
        String sign = pLpayClient.getSign(pickupUrl, receiveUrl, orderNo, orderAmount+"", orderCurrency);
        //转发请求到第三方支付，并返回支付路径
        String url = pLpayClient.sendRequestUrl(orderNo, orderCurrency, orderAmount+"", receiveUrl, pickupUrl, sign);

        //增加一条c2c订单记录
        C2cOrder c2cOrder=new C2cOrder();
        c2cOrder.setSn(orderNo);
        c2cOrder.setPayCurrency(srcCurrency);
        c2cOrder.setExchangeCurrency(tarCurrency);
        c2cOrder.setPrice(ratio);
        c2cOrder.setVolume(tarVolume);

        c2cOrder.setAmount(orderAmount);
        c2cOrder.setType(C2cConst.C2C_BUY);
        c2cOrder.setPayStatus(C2cConst.C2C_PAY_STATUS_NO_PAYMENT);
        c2cOrder.setPayStatusDesc(C2cConst.C2C_PAY_STATUS_DESC_RECHARGE);
         long payTime = Instant.now().toEpochMilli();
        c2cOrder.setPayTime(payTime);
        c2cOrder.setPayFinishTime(payTime);
        c2cOrder.setSynchStatus(C2cConst.C2C_SYNCH_STATUS_FALSE);
        c2cOrder.setApprovalStatus(C2cConst.C2C_APPROVAL_STATUS_IN_AUDIT);
        c2cOrder.setUserId(userId);
        c2cOrder.setCreated(payTime);
        c2cOrder.setModified(payTime);
        c2cOrder.setFrozenAsset(BigDecimal.ZERO);
        buyService.saveC2cOrder(c2cOrder);

        return url;
    }


}
