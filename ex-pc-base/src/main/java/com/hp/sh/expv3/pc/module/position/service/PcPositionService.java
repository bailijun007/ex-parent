package com.hp.sh.expv3.pc.module.position.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.pc.component.FaceValueQuery;
import com.hp.sh.expv3.pc.component.FeeCollectorSelector;
import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.constant.Precision;
import com.hp.sh.expv3.pc.constant.TradingRoles;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderDAO;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderTradeDAO;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.order.entity.PcOrderTrade;
import com.hp.sh.expv3.pc.module.order.mq.msg.MatchedMsg;
import com.hp.sh.expv3.pc.module.order.service.BaseValueCalc;
import com.hp.sh.expv3.pc.module.order.service.MarginRatioService;
import com.hp.sh.expv3.pc.module.position.dao.PcPositionDAO;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.symbol.dao.PcAccountSymbolDAO;
import com.hp.sh.expv3.pc.module.symbol.entity.PcAccountSymbol;
import com.hp.sh.expv3.pc.module.trade.entity.PcTrade;

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
	private FeeCollectorSelector feeCollectorSelector;
	
	@Autowired
	private PcAccountSymbolDAO pcAccountSymbolDAO;

	@Autowired
	private MarginRatioService marginRatioService;
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

		this.saveOrderTrade(matchedVo, order);
		
		if(actionType == OrderFlag.ACTION_OPEN){
			this.modPos(matchedVo, order);
		}else{
			
		}
	}

	private void saveOrderTrade(MatchedMsg matchedVo, PcOrder order) {
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
		orderTrade.setPnl(pnl);
		orderTrade.setPrice(matchedVo.getPrice());
		orderTrade.setSymbol(matchedVo.getSymbol());
		orderTrade.setTradeId(matchedVo.getTradeId());
		orderTrade.setTradeTime(matchedVo.getTradeTime());
		orderTrade.setUserId(order.getUserId());
		orderTrade.setVolume(null);
		this.pcOrderTradeDAO.save(orderTrade);
	}
	
	private void modPos(MatchedMsg matchedVo, PcOrder order) {
		PcAccountSymbol as = pcAccountSymbolDAO.lockUserSymbol(order.getUserId(), order.getAsset(), order.getSymbol());
		PcPosition pcPosition = this.getCurrentPosition(order.getUserId(), matchedVo.getAsset(), matchedVo.getSymbol(), order.getLongFlag());
		if(pcPosition==null){
			pcPosition = new PcPosition();
			pcPosition.setAsset(order.getAsset());
			pcPosition.setSymbol(order.getSymbol());
			pcPosition.setLongFlag(order.getLongFlag());
			pcPosition.setMarginMode(as.getMarginMode());
			pcPosition.setEntryLeverage(order.getLeverage());
			
			//
			pcPosition.setInitMargin(order.getOrderMargin());
			pcPosition.setHoldRatio(marginRatioService.getHoldRatio(order.getUserId(), order.getAsset(), order.getSymbol(), order.getVolume()));
			
			pcPosition.setVolume(order.getVolume());
			pcPosition.setBaseValue(BaseValueCalc.calcValue(order.getVolume().multiply(order.getFaceValue()), matchedVo.getPrice())); //TODO
			pcPosition.setPosMargin(FeeCalc.slope(order.getVolume(), matchedVo.getNumber(), order.getOrderMargin()));
			pcPosition.setMeanPrice(order.getPrice());
			pcPosition.setFeeCost(BigDecimal.ZERO);
			pcPosition.setLiqPrice(liqPrice);
			
			pcPosition.setLiqMarkPrice(liqMarkPrice);
			pcPosition.setLiqMarkTime(liqMarkTime);
			pcPosition.setLiqStatus(liqStatus);
			
			pcPosition.setRealisedPnl(realisedPnl);
			
		}

		//
		pcPosition.setInitMargin(initMargin);
		pcPosition.setHoldRatio(holdRatio);
		
		pcPosition.setVolume(volume);
		pcPosition.setAmount(amount);
		pcPosition.setPosMargin(posMargin);
		pcPosition.setEntryPrice(entryPrice);//avg
		pcPosition.setFeeCost(feeCost);
		pcPosition.setLiqPrice(liqPrice);
		
		pcPosition.setLiqMarkPrice(liqMarkPrice);
		pcPosition.setLiqMarkTime(liqMarkTime);
		pcPosition.setLiqStatus(liqStatus);
		
		pcPosition.setRealisedPnl(realisedPnl);
		
		this.pcAccountSymbolDAO.update(as);
	}
	
	private void cutPos(MatchedMsg matchedVo, PcOrder order) {
		
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

}
