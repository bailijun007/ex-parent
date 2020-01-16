package com.hp.sh.expv3.fund.extension.api;

import com.gitee.hupadev.base.api.ResultEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**api/callback/c2c
 * @author BaiLiJun  on 2020/1/15
 */
@Api(tags = "c2c订单回调通知")
@FeignClient(value = "ex-fund-account")
public interface C2cOrderCallbackApi {

    @ApiOperation(value = "订单成功回调通知")
    @RequestMapping(value = "/api/callback/c2c/deposit/notify")
    public String notify(@RequestParam("orderAmount") BigDecimal orderAmount, @RequestParam("orderCurrency") String orderCurrency,
                         @RequestParam("orderNo") String orderNo, @RequestParam(value = "paymentAmount", required = false) BigDecimal paymentAmount,
                         @RequestParam("sign") String sign, @RequestParam("signType") String signType,
                         @RequestParam("status") String status, @RequestParam("transactionId") String transactionId);

}
