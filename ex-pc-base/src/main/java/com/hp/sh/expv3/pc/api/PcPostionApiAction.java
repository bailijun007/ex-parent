package com.hp.sh.expv3.pc.api;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.pc.module.order.service.PcOrderService;
import com.hp.sh.expv3.pc.module.position.service.PcPositionMarginService;

@RestController
public class PcPostionApiAction implements PcPostionApi {
	
	@Autowired
	private PcOrderService pcOrderService;
	
	@Autowired
	private PcPositionMarginService pcPositionService;
	
	//调整保证金 TODO 加&减
	public void changeMargin(Long userId, String asset, String symbol, Integer longFlag, int optType, BigDecimal amount){
		pcPositionService.changeMargin(userId, asset, symbol, longFlag, optType, amount);
	}
	
	public boolean changeLeverage(long userId, String asset, String symbol, int marginMode, Integer longFlag, BigDecimal leverage){
		return pcPositionService.changeLeverage(userId, asset, symbol, marginMode, longFlag, leverage);
	}

}
