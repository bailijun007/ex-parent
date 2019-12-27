package com.hp.sh.expv3.fund.cash.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags ="充值地址接口")
@FeignClient(value = "ex-fund-account")
public interface DepositAddrApi {

	@ApiOperation("更新充值地址备注")
	@GetMapping(value = "/api/deposit_addr/updateRemark")
	boolean updateRemark(@RequestParam("userId") long userId, @RequestParam("asset") String asset, @RequestParam("depositAddrId") long depositAddrId, @RequestParam("remark") String remark);

}