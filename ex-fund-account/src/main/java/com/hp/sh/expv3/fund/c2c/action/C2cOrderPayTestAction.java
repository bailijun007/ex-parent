package com.hp.sh.expv3.fund.c2c.action;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.c2c.component.PLPayService;
import com.hp.sh.expv3.fund.c2c.component.PLpayClient;
import com.hp.sh.expv3.fund.c2c.entity.C2cOrder;
import com.hp.sh.expv3.fund.c2c.entity.NotifyParam;
import com.hp.sh.expv3.fund.c2c.service.BuyService;
import com.hp.sh.expv3.fund.extension.error.FundCommonError;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * @author BaiLiJun  on 2020/1/2
 */
@Api(tags = "C2c充值测试接口")
@RestController
@RequestMapping("/api/c2c/pay")
public class C2cOrderPayTestAction {
    @Autowired
    private PLPayService plPayService;


    @Autowired
    private BuyService buyService;


    private static final Logger logger = LoggerFactory.getLogger(C2cOrderPayTestAction.class);


    @ApiOperation(value = "创建c2c充值订单")
    @GetMapping("/deposit/create")
    public String create(@RequestParam("userId") long userId, @RequestParam("ratio") BigDecimal ratio,
                         @RequestParam("srcCurrency")  String srcCurrency,  @RequestParam("tarCurrency") String tarCurrency,
                         @RequestParam("tarVolume")  BigDecimal tarVolume,  @RequestParam("fabiAmt") BigDecimal fabiAmt){

        C2cOrder c2cOrder = plPayService.rujin(userId, ratio, srcCurrency, tarCurrency, tarVolume, fabiAmt);
        buyService.saveC2cOrder(c2cOrder);

        return "success";
    }

}
