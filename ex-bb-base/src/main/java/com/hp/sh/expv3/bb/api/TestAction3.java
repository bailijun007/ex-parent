package com.hp.sh.expv3.bb.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.service.BBOrderQueryService;
import com.hp.sh.expv3.bb.module.order.service.BBOrderUpdateService;
import com.hp.sh.expv3.utils.DbDateUtils;

import io.swagger.annotations.ApiOperation;

@RestController
public class TestAction3 {

	@Autowired
	private BBOrderQueryService orderQueryService;

	@Autowired
	private BBOrderUpdateService orderUpdateService;
	
	@ApiOperation(value = "测试updateOrder")
	@GetMapping(value = "/api/bb/test/updateOrder")
	public Object updateOrder() throws Exception{
		BBOrder order = this.orderQueryService.getOrder("USDT", "BTC_USDT", 1L, 178555896752933632L);
		orderUpdateService.setNewStatus(order, DbDateUtils.now());
		return 0;
	}
	
	@ApiOperation(value = "测试queryOrder")
	@GetMapping(value = "/api/bb/test/queryOrder")
	public Object queryOrder() throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("asset", "USDT");
		params.put("symbol", "BTC_USDT");
		params.put("createdStart", 1583474400000L);
		params.put("createdEnd", 1588737600000L);
		List<BBOrder> list = orderQueryService.queryList(params);
		return list;
	}
	
	@ApiOperation(value = "测试findOrder")
	@GetMapping(value = "/api/bb/test/findOrder")
	public Object findOrder() throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("asset", "USDT");
		params.put("symbol", "BTC_USDT");
		params.put("idList", Arrays.asList(178112105734638336L));
		List<BBOrder> list = orderQueryService.queryList(params);
		return list;
	}
	
}
