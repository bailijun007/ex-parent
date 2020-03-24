package com.hp.sh.expv3.pc.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.module.account.service.PcAccountCoreService;
import com.hp.sh.expv3.pc.module.order.service.PcOrderQueryService;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.position.service.PcPositionDataService;
import com.hp.sh.expv3.pc.module.position.service.PcPositionMarginService;
import com.hp.sh.expv3.pc.strategy.PcStrategyContext;
import com.hp.sh.expv3.pc.vo.response.ChangeMarginVo;
import com.hp.sh.expv3.pc.vo.response.CurPositionVo;
import com.hp.sh.expv3.utils.math.Precision;

@RestController
public class PcPostionApiAction implements PcPostionApi {
	
	@Autowired
	private PcPositionMarginService posMarginService;
	
	@Autowired
	private PcPositionDataService posDataService;
	
	@Autowired
	private PcAccountCoreService accountService;
	
	@Autowired
	private PcOrderQueryService orderQueryService;
	
	@Autowired
	private PcStrategyContext strategyContext;
	
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
	public boolean setAutoAddFlag(Long userId, String asset, String symbol, Integer longFlag, Integer autoAddFlag){
		return posMarginService.setAutoAddFlag(userId, asset, symbol, longFlag, autoAddFlag);
	}
	
	@Override
	public List<CurPositionVo> getCurrentPositionList(Long userId, String asset, String symbol){
		List<CurPositionVo> list = new ArrayList<CurPositionVo>();
		CurPositionVo openPos = this.getCurrentPosition(userId, asset, symbol, OrderFlag.ACTION_OPEN);
		if(openPos!=null){
			list.add(openPos);
		}
		CurPositionVo closePos = this.getCurrentPosition(userId, asset, symbol, OrderFlag.ACTION_CLOSE);
		if(closePos!=null){
			list.add(closePos);
		}
		return list;
	}
	
	public CurPositionVo getCurrentPosition(Long userId, String asset, String symbol, Integer longFlag){
		PcPosition pos = posDataService.getCurrentPosition(userId, asset, symbol, longFlag);
		if(pos==null){
			return null;
		}
		
		BigDecimal floatingPnl = strategyContext.calcFloatingPnl(pos);
		BigDecimal posMarginRatio = strategyContext.calPosMarginRatio(pos, floatingPnl);
		BigDecimal pnlRatio = pos.getRealisedPnl().divide(pos.getInitMargin(), Precision.PERCENT_PRECISION, Precision.LESS);
		
		CurPositionVo curPositionVo = new CurPositionVo();
		
		curPositionVo.setId(pos.getId());
		curPositionVo.setUserId(pos.getUserId());
		curPositionVo.setMarginMode(pos.getMarginMode());
		curPositionVo.setCreated(pos.getCreated());
		
		curPositionVo.setAsset(pos.getAsset());
		curPositionVo.setSymbol(pos.getSymbol());
		curPositionVo.setLongFlag(pos.getLongFlag());
		curPositionVo.setVolume(pos.getVolume());
		curPositionVo.setClosableVolume(orderQueryService.getClosingVolume(pos));
		curPositionVo.setPosMargin(pos.getPosMargin());
		curPositionVo.setLeverage(pos.getLeverage());
		curPositionVo.setMeanPrice(pos.getMeanPrice());
		curPositionVo.setPosMarginRatio(posMarginRatio);
		curPositionVo.setPnlRatio(pnlRatio);
		curPositionVo.setLiqPrice(pos.getLiqPrice());
		curPositionVo.setRealisedPnl(pos.getRealisedPnl());
		curPositionVo.setFloatingPnl(floatingPnl);
		curPositionVo.setHoldMarginRatio(pos.getHoldMarginRatio());
		curPositionVo.setAutoAddFlag(pos.getAutoAddFlag());
		curPositionVo.setFaceValue(pos.getFaceValue());
		curPositionVo.setBaseValue(pos.getBaseValue());
		
		return curPositionVo;
	}
}
