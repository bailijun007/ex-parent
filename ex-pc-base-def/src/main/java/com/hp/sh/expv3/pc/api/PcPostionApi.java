package com.hp.sh.expv3.pc.api;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	void changeMargin(@RequestParam("userId") Long userId, @RequestParam("asset") String asset, @RequestParam("symbol") String symbol, @RequestParam("longFlag") Integer longFlag, @RequestParam("optType") Integer optType, @RequestParam("amount") BigDecimal amount);
	
	@ApiOperation(value = "修改杠杆")
	@GetMapping(value = "/api/pc/position/leverage/change")
	public boolean changeLeverage(@RequestParam("userId") Long userId, @RequestParam("asset") String asset, @RequestParam("symbol") String symbol, @RequestParam("marginMode") Integer marginMode, @RequestParam("longFlag") Integer longFlag, @RequestParam("leverage") BigDecimal leverage);
	
}
