package com.hp.sh.expv3.pc.api;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.pc.module.account.service.PcAccountCoreService;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.position.service.PcPositionDataService;
import com.hp.sh.expv3.pc.module.position.service.PcPositionMarginService;
import com.hp.sh.expv3.pc.vo.response.ChangeMarginVo;

@RestController
public class PcPostionApiAction implements PcPostionApi {
	
	@Autowired
	private PcPositionMarginService posMarginService;
	
	@Autowired
	private PcPositionDataService posDataService;
	
	@Autowired
	private PcAccountCoreService accountService;
	
	public ChangeMarginVo showChangeMargin(Long userId, String asset, String symbol, Integer longFlag){
		PcPosition pos = this.posDataService.getCurrentPosition(userId, asset, symbol, longFlag);
		ChangeMarginVo vo = new ChangeMarginVo();
		vo.setLongFlag(longFlag);
		vo.setAutoAddFlag(pos.getAutoAddFlag());
		vo.setMaxReducedMargin(posMarginService.getMaxReducedMargin(pos));
		vo.setMaxIncreaseMargin(accountService.getBalance(userId, asset));
		return vo;
	}
	
	//调整保证金 加&减
	public void changeMargin(Long userId, String asset, String symbol, Integer longFlag, Integer optType, BigDecimal amount){
		posMarginService.changeMargin(userId, asset, symbol, longFlag, optType, amount);
	}
	
	public boolean changeLeverage(Long userId, String asset, String symbol, Integer marginMode, Integer longFlag, BigDecimal leverage){
		return posMarginService.changeLeverage(userId, asset, symbol, marginMode, longFlag, leverage);
	}
	
	//开关自动追加保证金
	@Override
	public boolean setAutoAddFlag(long userId, String asset, String symbol, Integer longFlag, Integer autoAddFlag){
		return posMarginService.setAutoAddFlag(userId, asset, symbol, longFlag, autoAddFlag);
	}
	
	public String getCurrentPosition(long userId, String asset, String symbol, Integer longFlag){
		PcPosition pos = posDataService.getCurrentPosition(userId, asset, symbol, longFlag);
		
		return null;
	}
}
