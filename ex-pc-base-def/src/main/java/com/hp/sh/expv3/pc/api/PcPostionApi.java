package com.hp.sh.expv3.pc.api;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 仓位接口
 * @author wangjg
 *
 */
@Api(tags="仓位接口")
@FeignClient(value="ex-pc-base")
public interface PcPostionApi {

	@ApiOperation(value = "增加保证金")
	@GetMapping(value = "/api/pc/position/margin/add")
	void addMargin(Long userId,String asset, String symbol, Integer longFlag, BigDecimal amount);
	
	@ApiOperation(value = "修改杠杆")
	@GetMapping(value = "/api/pc/position/leverage/change")
	public boolean changeLeverage(long userId, String asset, String symbol, int marginMode, Integer longFlag, BigDecimal leverage);
	
}
