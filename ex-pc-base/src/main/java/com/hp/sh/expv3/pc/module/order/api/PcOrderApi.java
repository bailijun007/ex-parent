package com.hp.sh.expv3.pc.module.order.api;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.pc.module.order.service.PcOrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags="订单接口")
@RestController
public class PcOrderApi {
	
	@Autowired
	private PcOrderService pcOrderService;
	
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
	 */
	@ApiOperation(value = "创建订单")
	@GetMapping(value = "/api/order/create")
	public void create(long userId, String cliOrderId, String asset, String symbol, int closeFlag, int longFlag, int timeInForce, BigDecimal price, BigDecimal amt){
		pcOrderService.create(userId, cliOrderId, asset, symbol, closeFlag, longFlag, timeInForce, price, amt);
	}

}
