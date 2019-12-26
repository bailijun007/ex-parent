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
    @GetMapping(value = "/api/extension/pc/trade/queryOrderTradeDetail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "orderId ", value = "委托id", example = "1", required = true)
    })
    List<PcOrderTradeDetailVo> queryOrderTradeDetail(@RequestParam("userId") Long userId, @RequestParam("asset") String asset,
                                                     @RequestParam("symbol") String symbol, @RequestParam("orderId") String orderId);


    @ApiOperation(value = "查询成交记录")
    @GetMapping(value = "/api/extension/pc/trade/queryTradeRecord")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型，多个以逗号分割", example = "BTC,ETH", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对，多个以逗号分割", example = "BTC_USDT,BTC_ETH", required = true),
            @ApiImplicitParam(name = "gtTradeId ", value = "成交记录id,请求大于trade_id的数据,gt和lt都填,以gt为准", example = "10", required = false),
            @ApiImplicitParam(name = "ltTradeId ", value = "成交记录id,请求小于trade_id的数据", example = "10", required = false),
            @ApiImplicitParam(name = "count ", value = "返回条数,最大100条", example = "10", required = false)
    })
    List<PcOrderTradeDetailVo> queryTradeRecord(@RequestParam(value = "asset") String asset,
                                                @RequestParam(value = "symbol", required = true) String symbol,
                                                @RequestParam(value = "gtTradeId", required = false) Long gtTradeId,
                                                @RequestParam(value = "ltTradeId", required = false) Long ltTradeId,
                                                @RequestParam(value = "count", required = true) Integer count);


}
