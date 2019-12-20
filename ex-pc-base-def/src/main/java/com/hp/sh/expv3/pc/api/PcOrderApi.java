package com.hp.sh.expv3.pc.api;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Api(tags="订单接口")
@FeignClient(value="ex-pc-base")
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
	void create(Long userId, String asset, String symbol, Integer closeFlag, Integer longFlag, Integer timeInForce,
			BigDecimal price, BigDecimal number, String cliOrderId) throws Exception;

	@ApiOperation(value = "取消订单")
	void cancel(Long userId, String asset, String symbol, Long orderId) throws Exception;

	@GetMapping(value = "/api/pc/order/bookReset")
	void bookReset(String asset, String symbol) throws Exception;

}