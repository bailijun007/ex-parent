package com.hp.sh.expv3.bb.api;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


@Api(tags="订单接口")
@FeignClient(value="ex-bb-base")
public interface PcOrderApi {

	/**
	 * 创建订单
	 * @param userId 用户ID
	 * @param cliOrderId 客户端订单ID
	 * @param asset 资产
	 * @param symbol 合约品种
	 * @param closeFlag 是否平仓：开/平
	 * @param longFlag 多/空
	 * @param timeInForce 生效机制
	 * @param price 委托价格
	 * @param amt 委托金额
	 * @throws Exception 
	 */
	@ApiOperation(value = "创建订单")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
        @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
        @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USD", required = true),
        @ApiImplicitParam(name = "closeFlag", value = "开/平", example = "0", required = true),
        @ApiImplicitParam(name = "longFlag", value = "是否：1-多仓，0-空仓", example = "1", required = true),
        @ApiImplicitParam(name = "timeInForce", value = "成交有效时间", example = "0", required = true),
        @ApiImplicitParam(name = "price", value = "价格", example = "8000", required = true),
        @ApiImplicitParam(name = "number", value = "数量（张）", example = "10", required = true),
        @ApiImplicitParam(name = "cliOrderId", value = "客户端订单ID", example = "12345", required = true)
    })
	@GetMapping(value = "/api/bb/order/create")
	Long create(@RequestParam("userId") Long userId, @RequestParam("asset") String asset, @RequestParam("symbol") String symbol, @RequestParam("closeFlag") Integer closeFlag, @RequestParam("longFlag") Integer longFlag, @RequestParam("timeInForce") Integer timeInForce,
			@RequestParam("price") BigDecimal price, @RequestParam("number") BigDecimal number, @RequestParam("cliOrderId") String cliOrderId);

	@ApiOperation(value = "取消订单")
	@GetMapping(value = "/api/bb/order/cancel")
	void cancel(@RequestParam("userId") Long userId, @RequestParam("asset") String asset, @RequestParam("symbol") String symbol, @RequestParam("orderId") Long orderId);

	@ApiOperation(value = "重置深度1")
	@GetMapping(value = "/api/bb/order/bookReset")
	void bookReset(@RequestParam("asset") String asset, @RequestParam("symbol") String symbol);

	@ApiOperation(value = "获取最大可开仓数量")
	@GetMapping(value = "/api/bb/order/maxOpenVolume")
	BigDecimal getMaxOpenVolume(@RequestParam("userId") Long userId, @RequestParam("asset") String asset, @RequestParam("symbol") String symbol, @RequestParam("longFlag") Long longFlag, @RequestParam("leverage") BigDecimal leverage);

}