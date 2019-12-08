package com.hp.sh.expv3.pc.module.position.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.pc.calc.BaseValueCalc;
import com.hp.sh.expv3.pc.calc.FeeCalc;
import com.hp.sh.expv3.pc.calc.PcPriceCalc;
import com.hp.sh.expv3.pc.calc.PnlCalc;
import com.hp.sh.expv3.pc.component.FeeCollectorSelector;
import com.hp.sh.expv3.pc.constant.LiqStatus;
import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.constant.Precision;
import com.hp.sh.expv3.pc.constant.TradingRoles;
import com.hp.sh.expv3.pc.module.account.service.impl.PcAccountCoreService;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderDAO;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderTradeDAO;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.order.entity.PcOrderTrade;
import com.hp.sh.expv3.pc.module.order.service.MarginRatioService;
import com.hp.sh.expv3.pc.module.position.dao.PcPositionDAO;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.symbol.dao.PcAccountSymbolDAO;
import com.hp.sh.expv3.pc.module.symbol.entity.PcAccountSymbol;
import com.hp.sh.expv3.pc.module.trade.entity.PcTrade;
import com.hp.sh.expv3.pc.mq.msg.MatchedMsg;
import com.hp.sh.expv3.utils.IntBool;

@Service
@Transactional
public class PcPositionService {

	@Autowired
	private PcPositionDAO pcPositionDAO;
	
	@Autowired
	private PcOrderTradeDAO pcOrderTradeDAO;
	
	@Autowired
	private PcOrderDAO pcOrderDAO;
	
	@Autowired
	private PcAccountSymbolDAO pcAccountSymbolDAO;

	@Autowired
	private MarginRatioService marginRatioService;
	
	@Autowired
	private PcPriceCalc pcPriceCalc;
	
	@Autowired
	private PnlCalc pnlCalc;
	
	@Autowired
	private PcAccountCoreService pcAccountCoreService;
	
	@Autowired
	private FeeCollectorSelector feeCollectorSelector;
	
	
	/**
	 * 处理成交
	 */
	public void handleTrade(PcTrade pcTrade){
		//taker
		MatchedMsg takerMatchedVo = new MatchedMsg();
		takerMatchedVo.setMakerFlag(TradingRoles.TAKER);
		takerMatchedVo.setAsset(pcTrade.getAsset());
		takerMatchedVo.setSymbol(pcTrade.getSymbol());
		takerMatchedVo.setPrice(pcTrade.getPrice());
		takerMatchedVo.setNumber(pcTrade.getNumber());
		takerMatchedVo.setTradeId(pcTrade.getId());
		takerMatchedVo.setTradeTime(pcTrade.getTradeTime());
		
		takerMatchedVo.setAccountId(pcTrade.getTkAccountId());
		takerMatchedVo.setMatchTxId(pcTrade.getMatchTxId());
		takerMatchedVo.setOrderId(pcTrade.getTkOrderId());
		
		//maker
		MatchedMsg makerMatchedVo = new MatchedMsg();
		makerMatchedVo.setMakerFlag(TradingRoles.TAKER);
		makerMatchedVo.setAsset(pcTrade.getAsset());
		makerMatchedVo.setSymbol(pcTrade.getSymbol());
		makerMatchedVo.setPrice(pcTrade.getPrice());
		makerMatchedVo.setNumber(pcTrade.getNumber());
		makerMatchedVo.setTradeId(pcTrade.getId());
		makerMatchedVo.setTradeTime(pcTrade.getTradeTime());
		
		makerMatchedVo.setAccountId(pcTrade.getMkAccountId());
		makerMatchedVo.setMatchTxId(pcTrade.getMatchTxId());
		makerMatchedVo.setOrderId(pcTrade.getMkOrderId());

		//taker
		this.handleMatchedOrder(takerMatchedVo);
		
		//maker
		this.handleMatchedOrder(makerMatchedVo);
		
	}
	
	//处理成交订单
	public void handleMatchedOrder(MatchedMsg matchedVo){
		int tradingRole = matchedVo.getMakerFlag();
		
		PcOrder order = this.pcOrderDAO.findById(matchedVo.getAccountId(), matchedVo.getOrderId());
		this.chekOrderStatus(order, matchedVo);
		Integer actionType = order.getCloseFlag();
		
		if(actionType == OrderFlag.ACTION_OPEN){
			BigDecimal meanPrice = this.modOpenPos(matchedVo, order);
			this.saveOrderTrade(matchedVo, order, meanPrice);
		}else{
			BigDecimal meanPrice = this.modClosePos(matchedVo, order);
			this.saveOrderTrade(matchedVo, order, meanPrice);
		}
		//TODO
		//修改订单状态：部分成交
		
	}

	private void saveOrderTrade(MatchedMsg matchedVo, PcOrder order, BigDecimal meanPrice) {
		BigDecimal feeRatio ;
		BigDecimal fee;
		if(order.getLongFlag()==OrderFlag.ACTION_OPEN){
			feeRatio = order.getOpenFeeRatio();
			fee = FeeCalc.slope(order.getVolume(), matchedVo.getNumber(), order.getOpenFee());
		}else{
			feeRatio = order.getCloseFee();
			fee = BigDecimal.ZERO;
		}
		
		Date now = new Date();
		PcOrderTrade orderTrade = new PcOrderTrade();
		orderTrade.setAsset(matchedVo.getAsset());
		orderTrade.setCreated(now);
		orderTrade.setFee(fee);
		orderTrade.setFeeCollectorId(feeCollectorSelector.getFeeCollectorId(order.getUserId(), order.getAsset(), order.getSymbol()));
		orderTrade.setFeeRatio(feeRatio);
		orderTrade.setId(null);
		orderTrade.setMakerFlag(matchedVo.getMakerFlag());
		orderTrade.setModified(now);
		orderTrade.setOrderId(order.getId());
		
		if(IntBool.isTrue(order.getCloseFlag())){
			BigDecimal pnl = pnlCalc.calcPnl(order.getLongFlag(), matchedVo.getNumber().multiply(order.getFaceValue()), meanPrice, matchedVo.getPrice(), Precision.COMMON_PRECISION);
			orderTrade.setPnl(pnl);	
		}else{
			orderTrade.setPnl(BigDecimal.ZERO);
		}
		
		orderTrade.setPrice(matchedVo.getPrice());
		orderTrade.setSymbol(matchedVo.getSymbol());
		orderTrade.setTradeId(matchedVo.getTradeId());
		orderTrade.setTradeTime(matchedVo.getTradeTime());
		orderTrade.setUserId(order.getUserId());
		orderTrade.setVolume(matchedVo.getNumber());
		this.pcOrderTradeDAO.save(orderTrade);
	}
	
	private BigDecimal modOpenPos(MatchedMsg matchedVo, PcOrder order) {
		PcAccountSymbol as = pcAccountSymbolDAO.lockUserSymbol(order.getUserId(), order.getAsset(), order.getSymbol());
		PcPosition pcPosition = this.getCurrentPosition(order.getUserId(), matchedVo.getAsset(), matchedVo.getSymbol(), order.getLongFlag());

		// TODO,精度问题，如果全部成交完，则无需按比例，全部转移即可
		BigDecimal unfilledVolume = order.getVolume().subtract(order.getFilledVolume());
		boolean isOrderCompleted = (unfilledVolume.compareTo(matchedVo.getNumber()) ==0); 
		
		BigDecimal origManPrice = null;
		
		if(pcPosition==null){
			pcPosition = new PcPosition();
			pcPosition.setAsset(order.getAsset());
			pcPosition.setSymbol(order.getSymbol());
			pcPosition.setLongFlag(order.getLongFlag());
			pcPosition.setMarginMode(as.getMarginMode());
			pcPosition.setEntryLeverage(order.getLeverage());
			
			//
			pcPosition.setVolume(matchedVo.getNumber());
			pcPosition.setBaseValue(BaseValueCalc.calcValue(order.getVolume().multiply(order.getFaceValue()), matchedVo.getPrice())); //TODO
			pcPosition.setLeverage(order.getLeverage());
			pcPosition.setPosMargin(isOrderCompleted?order.getOrderMargin(): FeeCalc.slope(order.getVolume(), matchedVo.getNumber(), order.getOrderMargin()));
			pcPosition.setCloseFee(isOrderCompleted?order.getCloseFee():FeeCalc.slope(order.getVolume(), matchedVo.getNumber(), order.getCloseFee()));
			pcPosition.setAutoAddFlag(IntBool.NO);
			pcPosition.setMeanPrice(matchedVo.getPrice());
			pcPosition.setInitMargin(isOrderCompleted?order.getOrderMargin():FeeCalc.slope(order.getVolume(), matchedVo.getNumber(), order.getOrderMargin()));
			pcPosition.setHoldRatio(marginRatioService.getHoldRatio(order.getUserId(), order.getAsset(), order.getSymbol(), order.getVolume()));
			
			pcPosition.setRealisedPnl(BigDecimal.ZERO);
			
			BigDecimal releaseOpenFee = FeeCalc.slope(order.getVolume(), matchedVo.getNumber(), order.getOpenFee());
			pcPosition.setFeeCost(releaseOpenFee);
			
			BigDecimal amount = order.getFaceValue().multiply(matchedVo.getNumber());
			pcPosition.setLiqPrice(pcPriceCalc.calcLiqPrice(pcPosition.getHoldRatio(), IntBool.isTrue(pcPosition.getLongFlag()), pcPosition.getMeanPrice(), amount, pcPosition.getPosMargin(), Precision.COMMON_PRECISION));
			
			pcPosition.setLiqMarkPrice(null);
			pcPosition.setLiqMarkTime(null);
			pcPosition.setLiqStatus(LiqStatus.NO);
			
			this.pcPositionDAO.save(pcPosition);
		}else{
			origManPrice = pcPosition.getMeanPrice();
			//
			pcPosition.setVolume(pcPosition.getVolume().add(matchedVo.getNumber()));
			pcPosition.setBaseValue(pcPosition.getBaseValue().add(BaseValueCalc.calcValue(order.getVolume().multiply(order.getFaceValue()), matchedVo.getPrice()))); //TODO
//			pcPosition.setLeverage(order.getLeverage());
			pcPosition.setPosMargin(pcPosition.getPosMargin().add(isOrderCompleted?order.getOrderMargin(): FeeCalc.slope(order.getVolume(), matchedVo.getNumber(), order.getOrderMargin())));
			pcPosition.setCloseFee(pcPosition.getCloseFee().add(isOrderCompleted?order.getCloseFee():FeeCalc.slope(order.getVolume(), matchedVo.getNumber(), order.getCloseFee())));
//			pcPosition.setAutoAddFlag(IntBool.NO);
			
			BigDecimal amount = order.getFaceValue().multiply(matchedVo.getNumber());
			
			BigDecimal meanPrice = pcPriceCalc.calcEntryPrice(IntBool.isTrue(pcPosition.getLongFlag()), pcPosition.getBaseValue(), amount, Precision.COMMON_PRECISION);
			pcPosition.setMeanPrice(meanPrice);
			pcPosition.setInitMargin(pcPosition.getInitMargin().add(isOrderCompleted?order.getOrderMargin():FeeCalc.slope(order.getVolume(), matchedVo.getNumber(), order.getOrderMargin())));
//			pcPosition.setHoldRatio(marginRatioService.getHoldRatio(order.getUserId(), order.getAsset(), order.getSymbol(), order.getVolume()));
			
//			pcPosition.setRealisedPnl(BigDecimal.ZERO);
			
			pcPosition.setFeeCost(pcPosition.getFeeCost().add(FeeCalc.slope(order.getVolume(), matchedVo.getNumber(), order.getOpenFee())));
			
			pcPosition.setLiqPrice(pcPriceCalc.calcLiqPrice(pcPosition.getHoldRatio(), IntBool.isTrue(pcPosition.getLongFlag()), pcPosition.getMeanPrice(), amount, pcPosition.getPosMargin(), Precision.COMMON_PRECISION));
			
			this.pcPositionDAO.update(pcPosition);
		}

		this.pcAccountSymbolDAO.update(as);
		
		return origManPrice;
	}
	
	private BigDecimal modClosePos(MatchedMsg matchedVo, PcOrder order) {
		PcAccountSymbol as = pcAccountSymbolDAO.lockUserSymbol(order.getUserId(), order.getAsset(), order.getSymbol());
		PcPosition pcPosition = this.getCurrentPosition(order.getUserId(), matchedVo.getAsset(), matchedVo.getSymbol(), order.getLongFlag());

		// TODO,精度问题，如果全部成交完，则无需按比例，全部转移即可
		BigDecimal unfilledVolume = order.getVolume().subtract(order.getFilledVolume());
		boolean isOrderCompleted = (unfilledVolume.compareTo(matchedVo.getNumber()) ==0); 
		
		BigDecimal origManPrice = null;
		
		if(true){
			origManPrice = pcPosition.getMeanPrice();
			//
			pcPosition.setVolume(pcPosition.getVolume().subtract(matchedVo.getNumber()));
			pcPosition.setBaseValue(pcPosition.getBaseValue().subtract(BaseValueCalc.calcValue(order.getVolume().multiply(order.getFaceValue()), matchedVo.getPrice()))); //TODO 很难
//			pcPosition.setLeverage(order.getLeverage());
			if(pcPosition.getVolume().compareTo(BigDecimal.ZERO)!=0){ //TODO 优化
				pcPosition.setPosMargin(BigDecimal.ZERO);
				pcPosition.setCloseFee(BigDecimal.ZERO);
				pcPosition.setInitMargin(BigDecimal.ZERO);
			}else{
				pcPosition.setPosMargin(pcPosition.getPosMargin().subtract(FeeCalc.slope(order.getVolume(), matchedVo.getNumber(), order.getOrderMargin())));
				pcPosition.setCloseFee(pcPosition.getCloseFee().subtract(FeeCalc.slope(order.getVolume(), matchedVo.getNumber(), order.getCloseFee())));
				pcPosition.setInitMargin(pcPosition.getInitMargin().subtract(FeeCalc.slope(order.getVolume(), matchedVo.getNumber(), order.getOrderMargin())));
			}
//			pcPosition.setHoldRatio(marginRatioService.getHoldRatio(order.getUserId(), order.getAsset(), order.getSymbol(), order.getVolume()));
			
			pcPosition.setFeeCost(pcPosition.getFeeCost().add(FeeCalc.slope(order.getVolume(), matchedVo.getNumber(), order.getOpenFee())));
			
			BigDecimal pnl = pnlCalc.calcPnl(order.getLongFlag(), matchedVo.getNumber().multiply(order.getFaceValue()), origManPrice, matchedVo.getPrice(), Precision.COMMON_PRECISION);
			pcPosition.setRealisedPnl(pcPosition.getRealisedPnl().add(pnl));
			
//			pcPosition.setLiqPrice(pcPriceCalc.calcLiqPrice(pcPosition.getHoldRatio(), IntBool.isTrue(pcPosition.getLongFlag()), pcPosition.getMeanPrice(), amount, pcPosition.getPosMargin(), Precision.COMMON_PRECISION));
			
			this.pcPositionDAO.update(pcPosition);
		}
		
		this.pcAccountSymbolDAO.update(as);
		
		return origManPrice;
	}

	/**
	 * 获取当前仓位，如果没有则创建一个
	 * @param userId
	 * @param asset
	 * @param symbol
	 * @return
	 */
	private PcPosition getCurrentPosition(Long userId, String asset, String symbol, int longFlag){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("asset", asset);
		params.put("symbol", symbol);
		params.put("longFlag", longFlag);
		PcPosition pos = this.pcPositionDAO.queryOne(params);
		return pos;
	}

	//检查订单状态
	private void chekOrderStatus(PcOrder order, MatchedMsg matchedVo) {
		// TODO Auto-generated method stub
		
	}
	
	private void synchFee(Long tradeOrderId, Long feeCollectorId, BigDecimal fee){
		
	}

	public BigDecimal getClosablePos(Long userId, String asset, String symbol){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("asset", asset);
		params.put("symbol", symbol);
		params.put("SUM", "volume");
		BigDecimal amount = this.pcPositionDAO.queryAmount(params);
		if(amount==null){
			return BigDecimal.ZERO;
		}
		return amount;
	}
	
}
