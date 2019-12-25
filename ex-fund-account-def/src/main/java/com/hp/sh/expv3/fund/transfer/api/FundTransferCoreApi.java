package com.hp.sh.expv3.fund.transfer.api;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "资金划转")
@FeignClient(value = "ex-fund-account")
public interface FundTransferCoreApi {

	@ApiOperation(value = "资金划转")
	@GetMapping(value = "/api/fund/transfer/transfer")
	void transfer(
			@RequestParam("userId") Long userId, 
			@RequestParam("asset") String asset,
			@RequestParam("srcAccountType") Integer srcAccountType, 
			@RequestParam("targetAccountType") Integer targetAccountType,
			@RequestParam("amount") BigDecimal amount);

	@ApiOperation(value = "处理划转")
	@GetMapping(value = "/api/fund/transfer/handlePending")
	void handlePending();

	void handleOne(@RequestParam("userId") Long userId, @RequestParam("id") Long id);

}