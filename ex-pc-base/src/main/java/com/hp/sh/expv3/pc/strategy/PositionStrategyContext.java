package com.hp.sh.expv3.pc.strategy;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.base.exceptions.CommonError;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.component.FeeCollectorSelector;
import com.hp.sh.expv3.pc.component.FeeRatioService;
import com.hp.sh.expv3.pc.component.MarkPriceService;
import com.hp.sh.expv3.pc.component.MetadataService;
import com.hp.sh.expv3.pc.constant.LiqStatus;
import com.hp.sh.expv3.pc.constant.MarginMode;
import com.hp.sh.expv3.pc.error.PositonError;
import com.hp.sh.expv3.pc.module.account.service.PcAccountCoreService;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.symbol.entity.PcAccountSymbol;
import com.hp.sh.expv3.pc.strategy.aabb.AABBHoldPosStrategy;
import com.hp.sh.expv3.pc.strategy.aabb.AABBPositionStrategy;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.math.BigUtils;

@Component
public class PositionStrategyContext {
	
	@Autowired
	private FeeRatioService feeRatioService;
	
	@Autowired
	private MarkPriceService markPriceService;
	
	@Autowired
	private MetadataService metadataService;
	
	@Autowired
	private AABBHoldPosStrategy holdPosStrategy;


	private List<StrategyBundle> list;
	private Map<String,StrategyBundle> map;
	
	public BigDecimal calcInitMargin (PcPosition pos){
		long userId = pos.getUserId();
		String asset = pos.getAsset();
		String symbol = pos.getSymbol();
		int marginMode = pos.getMarginMode();
		Integer longFlag = pos.getLongFlag();
		BigDecimal leverage = pos.getLeverage();
        BigDecimal feeRatio = feeRatioService.getCloseFeeRatio(userId, pos.getAsset(), pos.getSymbol());

        /* **** 修改保证金 **** */
        BigDecimal amount = pos.getVolume().multiply(metadataService.getFaceValue(asset, symbol));
        BigDecimal markPrice = markPriceService.getCurrentMarkPrice(asset, symbol);
        //新的仓位保证金
        BigDecimal initMarginRatio = feeRatioService.getInitedMarginRatio(leverage); 
        BigDecimal initMargin = this.holdPosStrategy.calcInitMargin(longFlag, initMarginRatio, amount, feeRatio, pos.getMeanPrice(), markPrice);
        
        return initMargin;
	}
	
	public HoldPosStrategy getHoldPosStrategy(String symbol){
		StrategyBundle sb = map.get(symbol);
		return sb.getHoldPosStrategy();
	}
	
	public PositionStrategy getPositionStrategy(String symbol){
		StrategyBundle sb = map.get(symbol);
		return sb.getPositionStrategy();
	}
	
}
