package com.hp.sh.expv3.bb.api;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hp.sh.expv3.bb.vo.response.ActiveOrderVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


@Api(tags="订单接口")
@FeignClient(value="ex-bb-base")
public interface BBOrderApi {

	/**
	 * 创建订单
	 * @param userId 用户ID
	 * @param cliOrderId 客户端订单ID
	 * @param asset 资产
	 * @param symbol 合约品种
	 * @param bidFlag 买卖
	 * @param timeInForce 生效机制
	 * @param price 委托价格
	 * @param amt 委托金额
	 * @throws Exception 
	 */
	@ApiOperation(value = "创建订单")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
        @ApiImplicitParam(name = "asset", value = "资产类型", example = "USD", required = true),
        @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USD", required = true),
        @ApiImplicitParam(name = "bidFlag", value = "买卖", example = "0", required = true),
        @ApiImplicitParam(name = "timeInForce", value = "成交有效时间", example = "0", required = true),
        @ApiImplicitParam(name = "price", value = "价格", example = "8000", required = true),
        @ApiImplicitParam(name = "number", value = "数量", example = "0.1", required = true),
        @ApiImplicitParam(name = "cliOrderId", value = "客户端订单ID", example = "12345", required = true)
    })
	@GetMapping(value = "/api/bb/order/create")
	Long create(@RequestParam("userId") Long userId, @RequestParam("asset") String asset, @RequestParam("symbol") String symbol
			, @RequestParam("bidFlag") Integer bidFlag
			, @RequestParam("timeInForce") Integer timeInForce,
			@RequestParam("price") BigDecimal price, @RequestParam("number") BigDecimal number, @RequestParam("cliOrderId") String cliOrderId);

	@ApiOperation(value = "取消订单")
	@GetMapping(value = "/api/bb/order/cancel")
	void cancel(@RequestParam("userId") Long userId, @RequestParam("asset") String asset, @RequestParam("symbol") String symbol, @RequestParam("orderId") Long orderId);

	@ApiOperation(value = "重置深度1")
	@GetMapping(value = "/api/bb/order/bookReset")
	void bookReset(@RequestParam("asset") String asset, @RequestParam("symbol") String symbol);

	@ApiOperation(value = "获取活动委托")
	@GetMapping(value = "/api/bb/order/queryActiveList")
	List<ActiveOrderVo> queryActiveList(@RequestParam("userId") Long userId, @RequestParam("asset") String asset, @RequestParam("symbol") String symbol);

}