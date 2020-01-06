package com.hp.sh.expv3.fund.c2c.action;

import com.hp.sh.chainserver.client.NotifyCreateParams;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.c2c.component.PLPayService;
import com.hp.sh.expv3.fund.c2c.component.PLpayClient;
import com.hp.sh.expv3.fund.c2c.entity.C2cOrder;
import com.hp.sh.expv3.fund.c2c.entity.NotifyParam;
import com.hp.sh.expv3.fund.c2c.service.BuyService;
import com.hp.sh.expv3.fund.extension.error.FundCommonError;
import com.hp.sh.expv3.fund.wallet.api.FundAccountCoreApi;
import com.hp.sh.expv3.fund.wallet.vo.request.FundAddRequest;
import com.hp.sh.expv3.utils.math.Precision;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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

    @Autowired
    private FundAccountCoreApi fundAccountCoreApi;

    private static final Logger logger = LoggerFactory.getLogger(C2cOrderCallbackAction.class);

    ReadWriteLock lock = new ReentrantReadWriteLock();

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

        //c2c订单验证
//        C2cOrder c2cOrder1 = buyService.queryBySn(param.getOrderNo());
//        if (c2cOrder1 != null) {
            lock.writeLock().lock();
            try {
                //更新一条c2c订单记录
                C2cOrder c2cOrder = new C2cOrder();
                BigDecimal orderAmount = param.getOrderAmount();
                c2cOrder.setAmount(orderAmount);
                //计算USDT 就是 orderAmount* 0.975 / 你系统的CNY:USD汇率;
                BigDecimal volume = param.getOrderAmount().multiply(new BigDecimal("0.975")).divide(new BigDecimal("7"), Precision.COMMON_PRECISION, Precision.LESS).stripTrailingZeros();
                c2cOrder.setVolume(volume);
                //价格=总额/volume
                BigDecimal price = orderAmount.divide(volume, Precision.COMMON_PRECISION, Precision.LESS).stripTrailingZeros();
                c2cOrder.setPrice(price);
                if (param.getStatus().equals("success")) {
                    c2cOrder.setPayStatus(1);
                } else {
                    c2cOrder.setPayStatus(2);
                }
                c2cOrder.setPayFinishTime(Instant.now().toEpochMilli());
                c2cOrder.setSynchStatus(1);
                c2cOrder.setApprovalStatus(1);
                c2cOrder.setModified(Instant.now().toEpochMilli());
//                c2cOrder.setId(c2cOrder1.getId());
//                c2cOrder.setUserId(c2cOrder1.getUserId());
                //这里需要掉接口 通过sn获取id和userid ,暂时写死
                c2cOrder.setId(134240439896145920L);
                c2cOrder.setUserId(0L);
                buyService.updateByIdAndUserId(c2cOrder);

                // 调用价钱方法和增加流水记录
//                FundAddRequest request=new FundAddRequest();
//                request.setUserId(c2cOrder1.getUserId());
//                request.setAmount(orderAmount);
//                request.setAsset(param.getOrderCurrency());
//                request.setRemark("c2c充值");
//                request.setTradeNo(c2cOrder1.getSn());
//                request.setTradeType(1);
//                fundAccountCoreApi.add(request);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.writeLock().unlock();
            }

//        }




        return "success";

    }

    @ApiOperation(value = "交易完成跳转URL")
    @GetMapping("/deposit/tradeSuccessSkip")
    public String tradeSuccessSkip(){

        return "success";
    }

}
