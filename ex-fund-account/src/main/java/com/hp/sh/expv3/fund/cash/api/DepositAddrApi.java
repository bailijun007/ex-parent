package com.hp.sh.expv3.fund.cash.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags ="充值地址接口")
@FeignClient(value = "ex-fund-account")
public interface DepositAddrApi {

	@ApiOperation("更新充值地址备注")
	@GetMapping(value = "/api/deposit_addr/updateRemark")
	boolean updateRemark(long userId, String asset, long depositAddrId, String remark);

}