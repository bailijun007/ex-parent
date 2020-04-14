package com.hp.sh.expv3.fund.c2c.job;

import com.hp.sh.expv3.fund.c2c.constants.C2cConst;
import com.hp.sh.expv3.fund.c2c.entity.C2cOrder;
import com.hp.sh.expv3.fund.c2c.service.BuyService;
import com.hp.sh.expv3.fund.c2c.service.QueryService;
import com.hp.sh.expv3.fund.extension.vo.C2cOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * C2C订单支付超时
 *
 * @author BaiLiJun  on 2020/1/17
 */
@Component
@EnableScheduling
public class CancelC2corder {

    @Autowired
    private QueryService queryService;

    @Autowired
    private BuyService buyService;

    /**
     * 每隔20分钟扫描支付超时的订单，并更新支付状态为已取消
     */
    @Scheduled(cron = "0 0/20 * * * ?")
    public void cancelC2corder(){

         LocalDateTime localDateTime = LocalDateTime.now();

        //扫描所有的待支付订单
         List<C2cOrderVo> c2cOrderVos = queryService.queryByPayStatus(C2cConst.C2C_PAY_STATUS_NO_PAYMENT,C2cConst.C2C_PAY_STATUS_DESC_RECHARGE);
        for (C2cOrderVo c2cOrderVo : c2cOrderVos) {
             LocalDateTime payTime = Instant.ofEpochMilli(c2cOrderVo.getPayTime()).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
            if (payTime.plusMinutes(20).isBefore(localDateTime)) {
                //更新：待支付为超时已取消
                C2cOrder c2cOrder=new C2cOrder();
                c2cOrder.setPayStatus(C2cConst.C2C_PAY_STATUS_CANCELED);
                c2cOrder.setPayStatusDesc(C2cConst.C2C_PAY_STATUS_DESC_PAY_OVERTIME);
                c2cOrder.setSn(c2cOrderVo.getSn());
                c2cOrder.setUserId(c2cOrderVo.getUserId());
                buyService.updateBySnAndUserId(c2cOrder);
            }
        }

    }

}
