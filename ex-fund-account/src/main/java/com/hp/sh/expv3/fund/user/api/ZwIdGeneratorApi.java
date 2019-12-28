package com.hp.sh.expv3.fund.user.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags ="用户ID")
@FeignClient(value = "ex-fund-account")
public interface ZwIdGeneratorApi {

	@ApiOperation(value = "获取id")
	@GetMapping(value = "/api/user/getNextId")
	public Long getNextId();
	
}
