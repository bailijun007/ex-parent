package com.hp.sh.expv3.fund.c2c.action;

import com.gitee.hupadev.base.api.ResultEntity;
import com.hp.sh.expv3.fund.c2c.component.PLPayService;
import com.hp.sh.expv3.fund.c2c.service.BuyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/1/2
 */
@Api(tags = "C2c充值测试接口")
@RestController
@RequestMapping("/api/c2c/pay")
public class C2cOrderPayApiAction {
    @Autowired
    private PLPayService plPayService;


    @Autowired
    private BuyService buyService;


    private static final Logger logger = LoggerFactory.getLogger(C2cOrderPayApiAction.class);


    @ApiOperation(value = "创建c2c充值订单")
    @ResultEntity
    @GetMapping("/deposit/create")
    public String create(@RequestParam("userId") long userId, @RequestParam("ratio") BigDecimal ratio,
                         @RequestParam("srcCurrency")  String srcCurrency,  @RequestParam("tarCurrency") String tarCurrency,
                         @RequestParam("tarVolume")  BigDecimal tarVolume,  @RequestParam("fabiAmt") BigDecimal fabiAmt){

        String s = plPayService.rujin(userId, ratio, srcCurrency, tarCurrency, tarVolume, fabiAmt);

        return s;
    }

}
