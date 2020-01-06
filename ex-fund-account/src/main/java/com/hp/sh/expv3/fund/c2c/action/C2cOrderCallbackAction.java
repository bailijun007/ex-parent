package com.hp.sh.expv3.fund.c2c.action;

import com.hp.sh.chainserver.client.NotifyCreateParams;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.c2c.component.PLPayService;
import com.hp.sh.expv3.fund.c2c.component.PLpayClient;
import com.hp.sh.expv3.fund.c2c.entity.C2cOrder;
import com.hp.sh.expv3.fund.c2c.entity.NotifyParam;
import com.hp.sh.expv3.fund.c2c.service.BuyService;
import com.hp.sh.expv3.fund.extension.error.FundCommonError;
import com.hp.sh.expv3.utils.math.Precision;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/1/2
 */
@Api(tags = "C2c充值回调接口")
@RestController
@RequestMapping("/api/callback/c2c")
public class C2cOrderCallbackAction {
    @Autowired
    private PLPayService plPayService;

    @Autowired
    private PLpayClient pLpayClient;

    @Autowired
    private BuyService buyService;


    private static final Logger logger = LoggerFactory.getLogger(C2cOrderCallbackAction.class);


    @ApiOperation(value = "订单成功回调通知")
    @PostMapping("/deposit/notify")
    public String notify(@RequestBody NotifyParam param) {

        System.out.println("收到订单成功回调通知 = " + param.toString());
        logger.info("收到订单成功回调通知:{}", param);

        //验证签名是否是伪造的
        boolean sign = pLpayClient.getNotifySign(param);
        if (!sign) {
            throw new ExException(FundCommonError.ORDER_CALLBACK_NOTIFY_FIND_SIGN_ERROR);
        }

        //更新一条c2c订单记录
        C2cOrder c2cOrder=new C2cOrder();
        BigDecimal orderAmount = param.getOrderAmount();
        c2cOrder.setAmount(orderAmount);
        //计算USDT 就是 orderAmount* 0.975 / 你系统的CNY:USD汇率;
        BigDecimal volume = param.getOrderAmount().multiply(new BigDecimal("0.975")).divide(new BigDecimal("7"));
        c2cOrder.setVolume(volume);
        //价格=总额/volume
        BigDecimal price = orderAmount.divide(volume, Precision.COMMON_PRECISION, Precision.LESS).stripTrailingZeros();
        c2cOrder.setPrice(price);
        if(param.getStatus().equals("success")){
            c2cOrder.setPayStatus(1);
        }else {
            c2cOrder.setPayStatus(2);
        }
        c2cOrder.setPayFinishTime(Instant.now().toEpochMilli());
        c2cOrder.setSynchStatus(1);
        c2cOrder.setApprovalStatus(1);
        c2cOrder.setModified(Instant.now().toEpochMilli());
        c2cOrder.setSn(param.getOrderNo());
        buyService.update(c2cOrder);

        // TODO 调用价钱方法和增加流水记录


        return "success";

    }

}
