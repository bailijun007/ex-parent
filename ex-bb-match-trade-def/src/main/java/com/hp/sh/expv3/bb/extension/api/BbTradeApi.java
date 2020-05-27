package com.hp.sh.expv3.bb.extension.api;

import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * 永续合约_成交(撮合结果)
 *
 * @author BaiLiJun  on 2020/1/2
 */
@Api(tags = "永续合约_成交(撮合结果)")
@FeignClient(value = "ex-bb-match-trade")
public interface BbTradeApi {

    @ApiOperation(value = "查询最新成交列表(撮合结果)")
    @GetMapping(value = "/api/bb/trade/ext/queryTradeList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "userId ", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "count ", value = "返回数量（最多返回100条）", example = "10", required = true)
    })
    List<BbTradeVo> queryTradeList(@RequestParam("asset") String asset,
                                   @RequestParam("symbol") String symbol,
                                   @RequestParam("userId") Long userId,
                                   @RequestParam("count") Integer count);


    @ApiOperation(value = "通过一个时间区间获取数据，升序排列")
    @GetMapping(value = "/api/bb/trade/ext/selectTradeListByTimeInterval")
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


    @ApiOperation(value = "小于当前时间点的最大的一条数据")
    @GetMapping(value = "/api/bb/trade/ext/queryLastTradeByLtTime")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = true)
    })
    BbTradeVo queryLastTradeByLtTime(@RequestParam(value = "asset", required = true) String asset,
                                     @RequestParam(value = "symbol", required = true) String symbol,
                                     @RequestParam(value = "startTime") Long startTime);

    @ApiOperation(value = "查询最新成交记录(撮合结果)")
    @GetMapping(value = "/api/bb/trade/ext/queryLastTrade")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "count ", value = "返回数量（最多返回100条）", example = "1", required = true)
    })
    List<BbTradeVo> queryLastTrade(@RequestParam("asset") String asset,
                                   @RequestParam("symbol") String symbol,
                                   @RequestParam("count") Integer count);


}
