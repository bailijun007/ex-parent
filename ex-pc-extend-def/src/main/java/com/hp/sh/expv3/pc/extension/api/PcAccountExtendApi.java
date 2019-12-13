/**
 * 
 */
package com.hp.sh.expv3.pc.extension.api;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author wangjg
 */
@Api(tags = "合约账户扩展接口")
@FeignClient(value="ex-pc-extend")
public interface PcAccountExtendApi {

	@ApiOperation(value = "获取账户余额")
	@GetMapping(value = "/api/pc_extend/pc_account/balance")
	BigDecimal getBalance(@RequestParam("userId") Long userId, @RequestParam("asset") String asset);

}