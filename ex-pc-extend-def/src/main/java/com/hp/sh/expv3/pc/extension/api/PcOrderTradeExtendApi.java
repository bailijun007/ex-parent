package com.hp.sh.expv3.pc.extension.api;

import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeDetailVo;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeExtendVo;
import com.hp.sh.expv3.pc.extension.vo.PcTradeVo;
import com.hp.sh.expv3.pc.extension.vo.UserOrderVo;
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
 * @author BaiLiJun  on 2019/12/24
 */
@Api(tags = "永续合约用户订单成交记录扩展接口")
@FeignClient(value = "ex-pc-extend")
public interface PcOrderTradeExtendApi {

    @ApiOperation(value = "查询当前委托的交易记录")
    @GetMapping(value = "/api/extension/pc/orderTrade/queryOrderTradeDetail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "orderId ", value = "委托id", example = "1", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间",  required = false)
    })
    List<PcOrderTradeDetailVo> queryOrderTradeDetail(@RequestParam("userId") Long userId, @RequestParam("asset") String asset,
                                                     @RequestParam("symbol") String symbol, @RequestParam("orderId") String orderId,
                                                     @RequestParam(value = "startTime", required = false) Long startTime, @RequestParam(value = "endTime", required = false) Long endTime);


    @ApiOperation(value = "获取当前用户交易明细")
    @GetMapping(value = "/api/extension/pc/orderTrade/queryHistory")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "lastTradeId  ", value = "主键id,不传返回最新的20条数据", example = "1", required = false),
            @ApiImplicitParam(name = "nextPage", value = "-1.上一页,1.下一页", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize ", value = "页行数", example = "20", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间",  required = false)
    })
    List<PcOrderTradeDetailVo> queryHistory(@RequestParam("userId") Long userId, @RequestParam("asset") String asset,
                                            @RequestParam("symbol") String symbol, @RequestParam(value = "lastTradeId", required = false) Long lastTradeId,
                                            @RequestParam("nextPage") Integer nextPage, @RequestParam("pageSize") Integer pageSize,
                                            @RequestParam(value = "startTime", required = false) Long startTime, @RequestParam(value = "endTime", required = false) Long endTime);


    @ApiOperation(value = "查询成交记录")
    @GetMapping(value = "/api/extension/pc/orderTrade/queryTradeRecord")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型，多个以逗号分割", example = "BTC,ETH", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对，多个以逗号分割", example = "BTC_USDT,BTC_ETH", required = true),
            @ApiImplicitParam(name = "gtTradeId ", value = "成交记录id,请求大于trade_id的数据,gt和lt都填,以gt为准", example = "10", required = false),
            @ApiImplicitParam(name = "ltTradeId ", value = "成交记录id,请求小于trade_id的数据", example = "10", required = false),
            @ApiImplicitParam(name = "count ", value = "返回条数,最大100条", example = "10", required = false),
            @ApiImplicitParam(name = "startTime", value = "开始时间",  required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = false)
    })
    List<PcOrderTradeDetailVo> queryTradeRecord(
            @RequestParam(value = "asset") String asset,
            @RequestParam(value = "symbol", required = true) String symbol,
            @RequestParam(value = "gtTradeId", required = false) Long gtTradeId,
            @RequestParam(value = "ltTradeId", required = false) Long ltTradeId,
            @RequestParam(value = "count", required = true) Integer count,
            @RequestParam(value = "startTime", required = false) Long startTime,
            @RequestParam(value = "endTime", required = false) Long endTime);

    @ApiOperation(value = "查小于某个时间点的最大的一条记录")
    @GetMapping(value = "/api/extension/pc/orderTrade/selectLessTimeTrade")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "statTime ", value = "时间戳", required = true)
    })
    PcOrderTradeDetailVo selectLessTimeTrade(
            @RequestParam(value = "asset", required = true) String asset,
            @RequestParam(value = "symbol", required = true) String symbol,
            @RequestParam(value = "statTime", required = true) Long statTime
    );


    @ApiOperation(value = "查某个用户的所有成交记录")
    @GetMapping(value = "/api/extension/pc/orderTrade/selectAllTradeListByUser")
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


    @ApiOperation(value = "查询成交记录列表(后台admin接口)")
    @GetMapping(value = "/api/extension/pc/orderTrade/selectPcFeeCollectByAccountId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "statTime", value = "开始时间",  required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间",  required = true)
    })
    List<PcOrderTradeDetailVo> selectPcFeeCollectByAccountId(@RequestParam("asset") String asset, @RequestParam("symbol") String symbol,
                                                             @RequestParam("userId") Long userId, @RequestParam("statTime") Long statTime, @RequestParam("endTime") Long endTime);


    @ApiOperation(value = "查某个时间区间某个用户的成交记录(不传时间则默认查今天以前的所有数据)")
    @GetMapping(value = "/api/extension/pc/orderTrade/selectTradeListByUserId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "userId ", value = "用户id", required = true),
            @ApiImplicitParam(name = "startTime ", required = false),
            @ApiImplicitParam(name = "endTime ",  required = false)
    })
    List<PcOrderTradeExtendVo> selectTradeListByUserId(
            @RequestParam(value = "asset", required = true) String asset,
            @RequestParam(value = "symbol", required = true) String symbol,
            @RequestParam(value = "userId", required = true) Long userId,
            @RequestParam(value = "startTime", required = false) Long startTime,
            @RequestParam(value = "endTime", required = false) Long endTime
    );

    @ApiOperation(value = "查询交易手续费")
    @GetMapping(value = "/api/extension/pc/orderTrade/queryPcTradeFee")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId ", value = "用户id", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "makerFlag", value = "1-marker， 0-taker", example = "1", required = false),
            @ApiImplicitParam(name = "beginTime ", value = "开始时间", required = false),
            @ApiImplicitParam(name = "endTime ", value = "结束时间", required = false)
    })
    BigDecimal queryPcTradeFee(@RequestParam(value = "userId", required = true) Long userId, @RequestParam(value = "asset", required = true) String asset,
                               @RequestParam(value = "makerFlag", required = false) Integer makerFlag, @RequestParam(value = "beginTime", required = false) Long beginTime,
                               @RequestParam(value = "endTime", required = false) Long endTime);

}
