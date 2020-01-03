package com.hp.sh.expv3.fund.c2c.action;

import com.hp.sh.chainserver.client.NotifyCreateParams;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.c2c.component.PLPayService;
import com.hp.sh.expv3.fund.c2c.component.PLpayClient;
import com.hp.sh.expv3.fund.c2c.entity.NotifyParam;
import com.hp.sh.expv3.fund.extension.error.FundCommonError;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
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

    @ApiOperation(value = "订单成功回调通知")
    @PostMapping("/deposit/notify")
    public String notify(@RequestBody NotifyParam param) {
        //验证签名是否是伪造的
        boolean sign = pLpayClient.getNotifySign(param);
        if (!sign) {
            throw new ExException(FundCommonError.ORDER_CALLBACK_NOTIFY_FIND_SIGN_ERROR);
        }
        // TODO 调用价钱方法和增加流水记录
        return "success";

    }

}
