package com.hp.sh.expv3.fund.c2c.action;

import com.gitee.hupadev.base.api.ResultEntity;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.c2c.component.PLPayService;
import com.hp.sh.expv3.fund.c2c.component.PLpayClient;
import com.hp.sh.expv3.fund.c2c.constants.C2cConst;
import com.hp.sh.expv3.fund.c2c.entity.C2cOrder;
import com.hp.sh.expv3.fund.c2c.entity.NotifyParam;
import com.hp.sh.expv3.fund.c2c.service.BuyService;
import com.hp.sh.expv3.fund.constant.ApprovalStatus;
import com.hp.sh.expv3.fund.extension.api.C2cOrderCallbackApi;
import com.hp.sh.expv3.fund.extension.error.ExFundError;
import com.hp.sh.expv3.fund.wallet.api.FundAccountCoreApi;
import com.hp.sh.expv3.fund.wallet.constant.TradeType;
import com.hp.sh.expv3.fund.wallet.vo.request.FundAddRequest;
import com.hp.sh.expv3.utils.math.Precision;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author BaiLiJun  on 2020/1/2
 */
@RestController
public class C2cOrderCallbackAction implements C2cOrderCallbackApi {
    @Autowired
    private PLPayService plPayService;

    @Autowired
    private PLpayClient pLpayClient;

    @Autowired
    private BuyService buyService;

    @Autowired
    private FundAccountCoreApi fundAccountCoreApi;

    private static final Logger logger = LoggerFactory.getLogger(C2cOrderCallbackAction.class);

    @Value("${plpay.server.c2c_fee_ratio}")
    private String c2cFeeRatio;

//    ReadWriteLock lock = new ReentrantReadWriteLock();


    @Override
    public String notify(@RequestParam("orderAmount") BigDecimal orderAmount, @RequestParam("orderCurrency") String orderCurrency,
                         @RequestParam("orderNo") String orderNo, @RequestParam(value = "paymentAmount", required = false) BigDecimal paymentAmount,
                         @RequestParam("sign") String sign, @RequestParam("signType") String signType,
                         @RequestParam("status") String status, @RequestParam("transactionId") String transactionId) {

        NotifyParam param = new NotifyParam();
        getNotifyParam(orderAmount, orderCurrency, orderNo, paymentAmount, sign, signType, status, transactionId, param);

        logger.info("收到订单成功回调通知:{}", param);

        //验证签名是否是伪造的
        boolean b = pLpayClient.getNotifySign(param);
        if (!b) {
            throw new ExException(ExFundError.ORDER_CALLBACK_NOTIFY_FIND_SIGN_ERROR);
        }

        //c2c订单验证
        String[] split = orderNo.split("-");
        Long userId = Long.parseLong(split[1]);
        C2cOrder c2cOrder1 = buyService.queryBySnAndUserId(orderNo, userId);
        if (c2cOrder1 != null && param.getStatus().equals("success")) {
//            lock.writeLock().lock();

                //更新一条c2c订单记录
                C2cOrder c2cOrder = new C2cOrder();
//                c2cOrder.setAmount(orderAmount);
                //计算USDT 就是 orderAmount*( 1 - 手续费率) / 你系统的CNY:USD汇率;
//                BigDecimal feeRatio = BigDecimal.ONE.subtract(new BigDecimal(c2cFeeRatio));
//                BigDecimal qty = param.getOrderAmount().multiply(feeRatio).divide(c2cOrder1.getPrice(), Precision.COMMON_PRECISION, Precision.LESS).stripTrailingZeros();
//                c2cOrder.setVolume(qty);
                c2cOrder.setPayStatus(C2cConst.C2C_PAY_STATUS_PAY_SUCCESS);
                c2cOrder.setPayFinishTime(Instant.now().toEpochMilli());
                c2cOrder.setSynchStatus(C2cConst.C2C_SYNCH_STATUS_TRUE);
                c2cOrder.setApprovalStatus(C2cConst.C2C_APPROVAL_STATUS_PASS);
                c2cOrder.setModified(Instant.now().toEpochMilli());
                c2cOrder.setSn(orderNo);
                c2cOrder.setUserId(userId);
                buyService.updateBySnAndUserId(c2cOrder);

                // 调用价钱方法和增加流水记录
                FundAddRequest request=new FundAddRequest();
                request.setUserId(userId);
                request.setAmount(c2cOrder1.getVolume());
                request.setAsset(c2cOrder1.getExchangeCurrency());
                request.setRemark(C2cConst.C2C_PAY_STATUS_DESC_RECHARGE);
                request.setTradeNo(c2cOrder1.getSn());
                request.setTradeType(TradeType.C2C_IN);
                fundAccountCoreApi.add(request);


        } else {
            throw new ExException(ExFundError.ORDER_CALLBACK_NOTIFY_FAIL);
        }
        return "success";
    }

    private void getNotifyParam(@RequestParam("orderAmount") BigDecimal orderAmount, @RequestParam("orderCurrency") String orderCurrency, @RequestParam("orderNo") String orderNo, @RequestParam(value = "paymentAmount", required = false) BigDecimal paymentAmount, @RequestParam("sign") String sign, @RequestParam("signType") String signType, @RequestParam("status") String status, @RequestParam("transactionId") String transactionId, NotifyParam param) {
        param.setOrderAmount(orderAmount);
        param.setOrderCurrency(orderCurrency);
        param.setOrderNo(orderNo);
        param.setPaymentAmount(paymentAmount);
        param.setSign(sign);
        param.setSignType(signType);
        param.setStatus(status);
        param.setTransactionId(transactionId);
    }


    @ApiOperation(value = "交易完成跳转URL")
    @ResultEntity
    @GetMapping("/deposit/tradeSuccessSkip")
    public String tradeSuccessSkip() {
        logger.info("进入交易完成跳转接口");
        return "success";
    }

}
