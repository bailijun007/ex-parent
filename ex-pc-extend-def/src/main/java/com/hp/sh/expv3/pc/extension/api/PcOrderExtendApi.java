package com.hp.sh.expv3.pc.extension.api;

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


    @ApiOperation(value = "获取当前用户活动委托")
    @GetMapping(value = "/api/extension/pc/order/query")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "orderType ", value = "委托类型,1:限价,非必填 ,不填为全部类型"),
            @ApiImplicitParam(name = "longFlag", value = "是否：1-多仓，0-空仓", example = "1"),
            @ApiImplicitParam(name = "closeFlag", value = "是否:1-平仓,0-开", example = "1"),
            @ApiImplicitParam(name = "currentPage ", value = "当前页数", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize ", value = "页行数", example = "10", required = true)
    })
    List<UserOrderVo> query(@RequestParam("userId") Long userId, @RequestParam("asset") String asset,
                            @RequestParam("symbol") String symbol, @RequestParam(value = "orderType", required = false) Integer orderType,
                            @RequestParam(value = "longFlag", required = false) Integer longFlag, @RequestParam(value = "closeFlag", required = false) Integer closeFlag,
                            @RequestParam("currentPage") Integer currentPage, @RequestParam("pageSize") Integer pageSize);


    @ApiOperation(value = "获取当前用户活动委托")
    @GetMapping(value = "/api/extension/pc/order/queryHistory")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "orderType ", value = "委托类型,1:限价,非必填 ,不填为全部类型"),
            @ApiImplicitParam(name = "longFlag", value = "是否：1-多仓，0-空仓", example = "1"),
            @ApiImplicitParam(name = "closeFlag", value = "是否:1-平仓,0-开", example = "1"),
            @ApiImplicitParam(name = "currentPage ", value = "当前页数", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize ", value = "页行数", example = "10", required = true),
            @ApiImplicitParam(name = "lastOrderId ", value = "当前页最后一张委托的id", example = "10", required = false),
            @ApiImplicitParam(name = "nextPage ", value = " 翻页标记,-1 上一页,1.下一页 ", example = "1", required = true),
    })
    List<UserOrderVo> queryHistory(@RequestParam("userId") Long userId, @RequestParam(value = "asset") String asset,
                                          @RequestParam("symbol") String symbol, @RequestParam(value = "orderType", required = false) Integer orderType,
                                          @RequestParam(value = "longFlag", required = false) Integer longFlag, @RequestParam(value = "closeFlag", required = false) Integer closeFlag,
                                          @RequestParam("currentPage") Integer currentPage, @RequestParam("pageSize") Integer pageSize,
                                          @RequestParam(value = "lastOrderId", required = false) Long lastOrderId, @RequestParam("nextPage") Integer nextPage);


    @ApiOperation(value = "获取当前用户活动委托")
    @GetMapping(value = "/api/extension/pc/order/queryAll")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "status", value = "(1:待报 2:已报 4:待撤销 8:已撤销 16:部分成交 32:全部成交) 选填 不填为全部"),
            @ApiImplicitParam(name = "longFlag", value = "是否：1-多仓，0-空仓", example = "1"),
            @ApiImplicitParam(name = "closeFlag", value = "是否:1-平仓,0-开", example = "1"),
            @ApiImplicitParam(name = "currentPage ", value = "当前页数", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize ", value = "页行数", example = "10", required = true)
    })
    List<UserOrderVo> queryAll(@RequestParam("userId") Long userId, @RequestParam("asset") String asset,
                            @RequestParam("symbol") String symbol, @RequestParam(value = "status", required = false) Integer status,
                            @RequestParam(value = "longFlag", required = false) Integer longFlag, @RequestParam(value = "closeFlag", required = false) Integer closeFlag,
                            @RequestParam("currentPage") Integer currentPage, @RequestParam("pageSize") Integer pageSize);




}
