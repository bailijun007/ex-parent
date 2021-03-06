package com.hp.sh.expv3.pc.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.pc.module.symbol.service.PcAccountSymbolService;

@RestController
public class PcAccountSymbolApiAction implements PcAccountSymbolApi {
	
	@Autowired
	private PcAccountSymbolService pcAccountSymbolService;
	
	@Override
	public boolean create(Long userId, String asset, String symbol){
		pcAccountSymbolService.create(userId, asset, symbol);
		return true;
	}
	
	@Override
	public boolean changeMarginMode(Long userId, String asset, String symbol, Integer marginMode){
		pcAccountSymbolService.changeMarginMode(userId, asset, symbol, marginMode);
		return true;
	}

}
