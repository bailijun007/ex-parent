package com.hp.sh.expv3.pc.extension.api;

import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeDetailVo;
import com.hp.sh.expv3.pc.extension.vo.UserOrderVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/24
 */
@Api(tags = "永续合约用户订单成交记录扩展接口")
@FeignClient(value = "ex-pc-extend")
public interface PcOrderTradeExtendApi {

    @ApiOperation(value = "查询当前委托的交易记录")
    @GetMapping(value = "/api/extension/pc/trade/query")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "orderId ", value = "委托id", example = "1", required = true)
    })
    List<PcOrderTradeDetailVo> query(@RequestParam("userId") Long userId, @RequestParam("asset") String asset,
                                     @RequestParam("symbol") String symbol, @RequestParam("orderId") String orderId);


}
