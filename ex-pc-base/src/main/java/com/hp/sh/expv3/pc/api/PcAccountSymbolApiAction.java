package com.hp.sh.expv3.pc.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.pc.module.symbol.service.PcAccountSymbolService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags="用户交易对设置接口")
@RestController
public class PcAccountSymbolApiAction {
	
	@Autowired
	private PcAccountSymbolService pcAccountSymbolService;
	
	@ApiOperation(value = "创建用户交易对设置")
	@GetMapping(value = "/api/pc/account/symbol/create")
	public boolean create(Long userId, String asset, String symbol){
		pcAccountSymbolService.create(userId, asset, symbol);
		return true;
	}

}
