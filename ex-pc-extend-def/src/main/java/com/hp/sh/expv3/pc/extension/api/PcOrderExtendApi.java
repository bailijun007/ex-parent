package com.hp.sh.expv3.pc.extension.api;

import com.gitee.hupadev.base.api.PageResult;
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
 * @author BaiLiJun  on 2019/12/23
 */
@Api(tags = "永续合约订单（委托）扩展接口")
@FeignClient(value = "ex-pc-extend")
public interface PcOrderExtendApi {


    @ApiOperation(value = "查询委托")
    @GetMapping(value = "/api/extension/pc/order/queryOrderList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型，多个以逗号分割", example = "BTC,ETH", required = false),
            @ApiImplicitParam(name = "symbol", value = "交易对，多个以逗号分割", example = "BTC_USDT,BTC_ETH", required = false),
            @ApiImplicitParam(name = "status", value = "委托状态，多个以逗号分割"),
            @ApiImplicitParam(name = "gtOrderId ", value = "orderId,请求大于order_id的数据,gt和lt都填,以gt为准", example = "10", required = false),
            @ApiImplicitParam(name = "ltOrderId ", value = "orderId,请求小于order_id的数据", example = "10", required = false),
            @ApiImplicitParam(name = "count", value = "返回条数最大100条", example = "10", required = true)
    })
    List<UserOrderVo> queryOrderList(@RequestParam(value = "userId") Long userId, @RequestParam(value = "asset", required = false) String asset,
                                     @RequestParam(value = "symbol", required = false) String symbol, @RequestParam(value = "gtOrderId", required = false) Long gtOrderId,
                                     @RequestParam(value = "ltOrderId", required = false) Long ltOrderId, @RequestParam("count") Integer count,
                                     @RequestParam("status") String status);


    @ApiOperation(value = "获取当前用户活动委托")
    @GetMapping(value = "/api/extension/pc/order/queryUserActivityOrder")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = false),
            @ApiImplicitParam(name = "orderType ", value = "委托类型,1:限价,非必填 ,不填为全部类型"),
            @ApiImplicitParam(name = "longFlag", value = "是否：1-多仓，0-空仓", example = "1"),
            @ApiImplicitParam(name = "closeFlag", value = "是否:1-平仓,0-开", example = "1"),
            @ApiImplicitParam(name = "isTotalNumber", value = "是否需要总条数:1-需要,0-不需要", example = "1"),
            @ApiImplicitParam(name = "currentPage ", value = "当前页数", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize ", value = "页行数", example = "10", required = true),
            @ApiImplicitParam(name = "lastOrderId ", value = "当前页最后一张委托的id", example = "10", required = false),
            @ApiImplicitParam(name = "nextPage ", value = " 翻页标记,-1 上一页,1.下一页 ", example = "1", required = true)
    })
    PageResult<UserOrderVo> queryUserActivityOrder(@RequestParam("userId") Long userId,
                                           @RequestParam("asset") String asset,
                                           @RequestParam(value = "symbol",required = false) String symbol,
                                           @RequestParam(value = "orderType", required = false) Integer orderType,
                                           @RequestParam(value = "longFlag", required = false) Integer longFlag,
                                           @RequestParam(value = "closeFlag", required = false) Integer closeFlag,
                                           @RequestParam("isTotalNumber") Integer isTotalNumber,
                                           @RequestParam("currentPage") Integer currentPage,
                                           @RequestParam(value = "pageSize",defaultValue = "20") Integer pageSize,
                                           @RequestParam(value = "lastOrderId", required = false) Long lastOrderId,
                                           @RequestParam("nextPage") Integer nextPage);


    @ApiOperation(value = "获取当前用户历史委托")
    @GetMapping(value = "/api/extension/pc/order/queryHistory")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = false),
            @ApiImplicitParam(name = "orderType ", value = "委托类型,1:限价,非必填 ,不填为全部类型"),
            @ApiImplicitParam(name = "longFlag", value = "是否：1-多仓，0-空仓", example = "1"),
            @ApiImplicitParam(name = "closeFlag", value = "是否:1-平仓,0-开", example = "1"),
            @ApiImplicitParam(name = "currentPage ", value = "当前页数", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize ", value = "页行数", example = "10", required = true),
            @ApiImplicitParam(name = "lastOrderId ", value = "当前页最后一张委托的id", example = "10", required = false),
            @ApiImplicitParam(name = "nextPage ", value = " 翻页标记,-1 上一页,1.下一页 ", example = "1", required = true)
    })
    List<UserOrderVo> queryHistory(@RequestParam("userId") Long userId,
                                   @RequestParam(value = "asset") String asset,
                                   @RequestParam(value = "symbol", required = false) String symbol,
                                   @RequestParam(value = "orderType", required = false) Integer orderType,
                                   @RequestParam(value = "longFlag", required = false) Integer longFlag,
                                   @RequestParam(value = "closeFlag", required = false) Integer closeFlag,
                                   @RequestParam("currentPage") Integer currentPage,
                                   @RequestParam("pageSize") Integer pageSize,
                                   @RequestParam(value = "lastOrderId", required = false) Long lastOrderId,
                                   @RequestParam("nextPage") Integer nextPage);


    @ApiOperation(value = "获取当前用户所有委托,条件查询")
    @GetMapping(value = "/api/extension/pc/order/queryAll")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "status", value = "(1:待报 2:已报 4:待撤销 8:已撤销 16:部分成交 32:全部成交) 选填 不填为全部"),
            @ApiImplicitParam(name = "longFlag", value = "是否：1-多仓，0-空仓", example = "1"),
            @ApiImplicitParam(name = "closeFlag", value = "是否:1-平仓,0-开", example = "1"),
            @ApiImplicitParam(name = "currentPage ", value = "当前页数", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize ", value = "页行数", example = "10", required = true),
            @ApiImplicitParam(name = "lastOrderId ", value = "当前页最后一张委托的id", example = "10", required = false),
            @ApiImplicitParam(name = "nextPage ", value = " 翻页标记,-1 上一页,1.下一页 ", example = "1", required = true)
    })
    List<UserOrderVo> queryAll(@RequestParam("userId") Long userId,
                               @RequestParam("asset") String asset,
                               @RequestParam("symbol") String symbol,
                               @RequestParam(value = "status", required = false) Integer status,
                               @RequestParam(value = "longFlag", required = false) Integer longFlag,
                               @RequestParam(value = "closeFlag", required = false) Integer closeFlag,
                               @RequestParam("currentPage") Integer currentPage,
                               @RequestParam("pageSize") Integer pageSize,
                               @RequestParam("lastOrderId") Long lastOrderId,
                               @RequestParam("nextPage") Integer nextPage);


    @ApiOperation(value = "订单列表查询(后台admin接口)")
    @GetMapping(value = "/api/extension/pc/order/pageQueryOrderList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = false),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = false),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = false),
            @ApiImplicitParam(name = "orderId", value = "委托id", example = "1", required = false),
            @ApiImplicitParam(name = "status", value = "(1:待报 2:已报 4:待撤销 8:已撤销 16:部分成交 32:全部成交) 选填 不填为全部"),
            @ApiImplicitParam(name = "closeFlag", value = "是否:1-平仓,0-开", example = "1"),
            @ApiImplicitParam(name = "pageNo ", value = "当前页数", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize ", value = "页行数", example = "10", required = true)
    })
    PageResult<UserOrderVo> pageQueryOrderList(@RequestParam(value = "userId", required = false) Long userId,
                                           @RequestParam(value = "asset", required = false) String asset,
                                           @RequestParam(value = "symbol", required = false) String symbol,
                                           @RequestParam(value = "status", required = false) Integer status,
                                           @RequestParam(value = "closeFlag", required = false) Integer closeFlag,
                                           @RequestParam("orderId") Long orderId,
                                           @RequestParam("pageNo") Integer pageNo,
                                           @RequestParam("pageSize") Integer pageSize);


}
