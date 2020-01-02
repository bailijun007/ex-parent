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



    @ApiOperation(value = "查询成交记录")
    @GetMapping(value = "/api/extension/pc/trade/queryTradeRecord")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型，多个以逗号分割", example = "BTC,ETH", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对，多个以逗号分割", example = "BTC_USDT,BTC_ETH", required = true),
            @ApiImplicitParam(name = "gtTradeId ", value = "成交记录id,请求大于trade_id的数据,gt和lt都填,以gt为准", example = "10", required = false),
            @ApiImplicitParam(name = "ltTradeId ", value = "成交记录id,请求小于trade_id的数据", example = "10", required = false),
            @ApiImplicitParam(name = "count ", value = "返回条数,最大100条", example = "10", required = false)
    })
    List<PcOrderTradeDetailVo> queryTradeRecord(
            @RequestParam(value = "asset") String asset,
            @RequestParam(value = "symbol", required = true) String symbol,
            @RequestParam(value = "gtTradeId", required = false) Long gtTradeId,
            @RequestParam(value = "ltTradeId", required = false) Long ltTradeId,
            @RequestParam(value = "count", required = true) Integer count);


    @ApiOperation(value = "查询最新成交记录")
    @GetMapping(value = "/api/extension/pc/trade/queryLastTradeRecord")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型，多个以逗号分割", example = "BTC,ETH", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对，多个以逗号分割", example = "BTC_USDT,BTC_ETH", required = true),
             @ApiImplicitParam(name = "count ", value = "返回条数（最大100条）", example = "10", required = true)
    })
    List<PcOrderTradeDetailVo> queryLastTradeRecord(
            @RequestParam(value = "asset") String asset,
            @RequestParam(value = "symbol", required = true) String symbol,
            @RequestParam(value = "count", required = true) Integer count);



    @ApiOperation(value = "查小于某个时间点的最大的一条记录")
    @GetMapping(value = "/api/extension/pc/trade/selectLessTimeTrade")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = false),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = false),
            @ApiImplicitParam(name = "statTime ", value = "时间戳", required = false)
    })
    PcOrderTradeDetailVo selectLessTimeTrade(
            @RequestParam(value = "asset", required = false) String asset,
            @RequestParam(value = "symbol", required = false) String symbol,
            @RequestParam(value = "statTime", required = false) Long statTime
    );



    @ApiOperation(value = "查某个用户的所有成交记录")
    @GetMapping(value = "/api/extension/pc/trade/selectAllTradeListByUser")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = false),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = false),
            @ApiImplicitParam(name = "userId ", value = "用户id", required = true)
    })
    List<PcOrderTradeDetailVo> selectAllTradeListByUser(
            @RequestParam(value = "asset", required = false) String asset,
            @RequestParam(value = "symbol", required = false) String symbol,
            @RequestParam(value = "userId", required = true) Long userId
    );


    @ApiOperation(value = "通过一个时间区间获取数据")
    @GetMapping(value = "/api/extension/pc/trade/selectTradeListByTimeInterval")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = false),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = false),
            @ApiImplicitParam(name = "statTime ", value = "开始时间", required = false),
            @ApiImplicitParam(name = "endTime ", value = "结束时间", required = false)
    })
    List<PcOrderTradeDetailVo> selectTradeListByTimeInterval(
            @RequestParam(value = "asset", required = false) String asset,
            @RequestParam(value = "symbol", required = false) String symbol,
            @RequestParam(value = "statTime", required = false) Long statTime,
            @RequestParam(value = "endTime", required = false) Long endTime
    );



    @ApiOperation(value = "查某个时间区间某个用户的成交记录")
    @GetMapping(value = "/api/extension/pc/trade/selectTradeListByUser")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = false),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = false),
            @ApiImplicitParam(name = "userId ", value = "用户id", required = true),
            @ApiImplicitParam(name = "statTime ", value = "开始时间", required = false),
            @ApiImplicitParam(name = "endTime ", value = "结束时间", required = false)
    })
    List<PcOrderTradeDetailVo> selectTradeListByUser(
            @RequestParam(value = "asset", required = false) String asset,
            @RequestParam(value = "symbol", required = false) String symbol,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "statTime", required = false) Long statTime,
            @RequestParam(value = "endTime", required = false) Long endTime
    );



}
