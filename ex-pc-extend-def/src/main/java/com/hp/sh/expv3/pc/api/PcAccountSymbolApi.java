package com.hp.sh.expv3.pc.api;

import org.springframework.cloud.openfeign.FeignClient;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags="用户交易对设置接口")
@FeignClient(value="ex-pc-base")
public interface PcAccountSymbolApi {

	@ApiOperation(value = "创建用户交易对设置")
	boolean create(Long userId, String asset, String symbol);

}