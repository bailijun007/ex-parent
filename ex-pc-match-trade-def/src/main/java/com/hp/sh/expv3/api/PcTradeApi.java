package com.hp.sh.expv3.api;

import com.hp.sh.expv3.vo.PcTradeVo;
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
@FeignClient(value = "ex-pc-match-trade")
public interface PcTradeApi {

    @ApiOperation(value = "查询最新成交记录(撮合结果)")
    @GetMapping(value = "/api/extension/pc/trade/queryLastTrade")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "count ", value = "返回数量（最多返回100条）", example = "1", required = true)
    })
    List<PcTradeVo> queryLastTrade(@RequestParam("asset") String asset,
                                   @RequestParam("symbol") String symbol,
                                   @RequestParam("count") Integer count);


    @ApiOperation(value = "大于等于这个时间点数据的最大(或最小)价格的数据")
    @GetMapping(value = "/api/extension/pc/trade/queryTradeByGtTime")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = false),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = false),
            @ApiImplicitParam(name = "type", value = "1:最大价格的数据，2：最小价格的数据", example = "1", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = true)
    })
    List<PcTradeVo> queryTradeByGtTime(@RequestParam(value = "asset", required = false) String asset,
                                       @RequestParam(value = "symbol", required = false) String symbol,
                                       @RequestParam(value = "type") Integer type,
                                       @RequestParam(value = "startTime") Long startTime);


    @ApiOperation(value = "小于某个时间点的最近的一条数据")
    @GetMapping(value = "/api/extension/pc/trade/queryLastTradeByLtTime")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = false),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = false),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = true)
    })
    PcTradeVo queryLastTradeByLtTime(@RequestParam(value = "asset", required = false) String asset,
                                     @RequestParam(value = "symbol", required = false) String symbol,
                                     @RequestParam(value = "startTime") Long startTime);


    @ApiOperation(value = "通过一个时间区间获取数据包含开始时间，升序排列")
    @GetMapping(value = "/api/extension/pc/trade/selectTradeListByTimeInterval")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = true)
    })
    List<PcTradeVo> selectTradeListByTimeInterval(@RequestParam(value = "asset", required = true) String asset,
                                                  @RequestParam(value = "symbol", required = true) String symbol,
                                                  @RequestParam(value = "startTime") Long startTime,
                                                  @RequestParam(value = "endTime") Long endTime);


    @ApiOperation(value = "查某个时间区间某个用户的成交记录(不传时间则默认查今天的数据)")
    @GetMapping(value = "/api/extension/pc/trade/selectTradeListByUser")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "userId ", value = "用户id", required = true),
            @ApiImplicitParam(name = "startTime ", value = "开始时间", required = false),
            @ApiImplicitParam(name = "endTime ", value = "结束时间", required = false)
    })
    List<PcTradeVo> selectTradeListByUser(
            @RequestParam(value = "asset", required = true) String asset,
            @RequestParam(value = "symbol", required = true) String symbol,
            @RequestParam(value = "userId", required = true) Long userId,
            @RequestParam(value = "startTime", required = false) Long startTime,
            @RequestParam(value = "endTime", required = false) Long endTime
    );

    @ApiOperation(value = "一段时间内总的成交量")
    @GetMapping(value = "/api/extension/pc/trade/getTotalTurnover")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = false),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = false),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = true)
    })
    BigDecimal getTotalTurnover(@RequestParam(value = "asset", required = false) String asset,
                                @RequestParam(value = "symbol", required = false) String symbol,
                                @RequestParam(value = "startTime") Long startTime,
                                @RequestParam(value = "endTime") Long endTime);



}
