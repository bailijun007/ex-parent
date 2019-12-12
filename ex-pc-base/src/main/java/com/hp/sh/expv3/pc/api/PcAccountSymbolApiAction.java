package com.hp.sh.expv3.pc.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.pc.module.symbol.service.PcAccountSymbolService;

@RestController
public class PcAccountSymbolApiAction implements PcAccountSymbolApi {
	
	@Autowired
	private PcAccountSymbolService pcAccountSymbolService;
	
	@Override
	@GetMapping(value = "/api/pc/account/symbol/create")
	public boolean create(Long userId, String asset, String symbol){
		pcAccountSymbolService.create(userId, asset, symbol);
		return true;
	}

}
