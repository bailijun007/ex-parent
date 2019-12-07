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
import com.hp.sh.expv3.pc.component.PcBaseValueService;
import com.hp.sh.expv3.pc.component.PcPriceServiceAabbImpl;
import com.hp.sh.expv3.pc.constant.LiqStatus;
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
import com.hp.sh.expv3.utils.IntBool;

@Service
@Transactional
public class PcPositionService2 {

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
	
	@Autowired
	private PcPriceServiceAabbImpl pcPriceServiceAabbImpl;
	
	@Autowired
	private PcBaseValueService baseValueCalc;
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
		
		if(actionType == OrderFlag.ACTION_CLOSE){
			BigDecimal meanPrice = this.modClosePos(matchedVo, order);
			this.saveOrderTrade(matchedVo, order, meanPrice);
		}else{
			
		}
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
			BigDecimal pnl = baseValueCalc.calcPnl(order.getLongFlag(), matchedVo.getNumber().multiply(order.getFaceValue()), meanPrice, matchedVo.getPrice(), Precision.COMMON_PRECISION);
			orderTrade.setPnl(pnl);	
		}else{
			orderTrade.setPnl(BigDecimal.ZERO);
		}
		
		orderTrade.setPrice(matchedVo.getPrice());
		orderTrade.setSymbol(matchedVo.getSymbol());
		orderTrade.setTradeId(matchedVo.getTradeId());
		orderTrade.setTradeTime(matchedVo.getTradeTime());
		orderTrade.setUserId(order.getUserId());
		orderTrade.setVolume(null);
		this.pcOrderTradeDAO.save(orderTrade);
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
				pcPosition.setPosMargin(pcPosition.getPosMargin().subtract(isOrderCompleted?order.getOrderMargin(): FeeCalc.slope(order.getVolume(), matchedVo.getNumber(), order.getOrderMargin())));
				pcPosition.setCloseFee(pcPosition.getCloseFee().subtract(isOrderCompleted?order.getCloseFee():FeeCalc.slope(order.getVolume(), matchedVo.getNumber(), order.getCloseFee())));
				pcPosition.setInitMargin(pcPosition.getInitMargin().subtract(isOrderCompleted?order.getOrderMargin():FeeCalc.slope(order.getVolume(), matchedVo.getNumber(), order.getOrderMargin())));
			}
//			pcPosition.setHoldRatio(marginRatioService.getHoldRatio(order.getUserId(), order.getAsset(), order.getSymbol(), order.getVolume()));
			
			pcPosition.setFeeCost(pcPosition.getFeeCost().add(FeeCalc.slope(order.getVolume(), matchedVo.getNumber(), order.getOpenFee())));
			
			BigDecimal pnl = baseValueCalc.calcPnl(order.getLongFlag(), matchedVo.getNumber().multiply(order.getFaceValue()), origManPrice, matchedVo.getPrice(), Precision.COMMON_PRECISION);
			pcPosition.setRealisedPnl(pcPosition.getRealisedPnl().add(pnl));
			
//			pcPosition.setLiqPrice(pcPriceServiceAabbImpl.calcLiqPrice(pcPosition.getHoldRatio(), IntBool.isTrue(pcPosition.getLongFlag()), pcPosition.getMeanPrice(), amount, pcPosition.getPosMargin(), Precision.COMMON_PRECISION));
			
			this.pcPositionDAO.update(pcPosition);
		}

		this.pcAccountSymbolDAO.update(as);
		
		return origManPrice;
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
