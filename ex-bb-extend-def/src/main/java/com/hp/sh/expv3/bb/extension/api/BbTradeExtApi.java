package com.hp.sh.expv3.bb.extension.api;

import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/2/15
 */
@Api(tags = "币币成交(撮合结果)接口")
@FeignClient(value = "ex-bb-extend")
public interface BbTradeExtApi {

    @ApiOperation(value = "通过一个时间区间获取数据，升序排列")
    @GetMapping(value = "/api/bbTrade/Ext/selectTradeListByTimeInterval")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = true)
    })
    List<BbTradeVo> selectTradeListByTimeInterval(@RequestParam(value = "asset", required = true) String asset,
                                                  @RequestParam(value = "symbol", required = true) String symbol,
                                                  @RequestParam(value = "startTime") Long startTime,
                                                  @RequestParam(value = "endTime") Long endTime);
}
