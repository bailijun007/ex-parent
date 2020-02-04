package com.hp.sh.expv3.bb.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.bb.api.BBAccountSymbolApi;
import com.hp.sh.expv3.bb.module.symbol.service.BBAccountSymbolService;

@RestController
public class BBAccountSymbolApiAction implements BBAccountSymbolApi {
	
	@Autowired
	private BBAccountSymbolService bBAccountSymbolService;
	
	@Override
	public boolean create(Long userId, String asset, String symbol){
		bBAccountSymbolService.create(userId, asset, symbol);
		return true;
	}
	
	@Override
	public boolean changeMarginMode(Long userId, String asset, String symbol, Integer marginMode){
		bBAccountSymbolService.changeMarginMode(userId, asset, symbol, marginMode);
		return true;
	}

}
