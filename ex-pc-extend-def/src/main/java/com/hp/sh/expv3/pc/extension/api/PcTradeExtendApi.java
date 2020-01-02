package com.hp.sh.expv3.pc.extension.api;

import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeDetailVo;
import com.hp.sh.expv3.pc.extension.vo.PcTradeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 永续合约_成交(撮合结果)
 * @author BaiLiJun  on 2020/1/2
 */
@Api(tags = "永续合约用户订单成交记录扩展接口")
@FeignClient(value = "ex-pc-extend")
public interface PcTradeExtendApi {

    @ApiOperation(value = "查询当前委托的成交记录(撮合结果)")
    @GetMapping(value = "/api/extension/pc/trade/queryLastTrade")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "count ", value = "返回数量（最多返回100条）", example = "1", required = true)
    })
    List<PcTradeVo> queryLastTrade( @RequestParam("asset") String asset,
                                     @RequestParam("symbol") String symbol,
                                    @RequestParam("count") Integer count);


}
