package com.hp.sh.expv3.pc.module.position.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.base.exceptions.CommonError;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.atemp.Question;
import com.hp.sh.expv3.pc.calc.PcPriceCalc;
import com.hp.sh.expv3.pc.component.FeeCollectorSelector;
import com.hp.sh.expv3.pc.component.MarginRatioService;
import com.hp.sh.expv3.pc.constant.LiqStatus;
import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.constant.PcAccountTradeType;
import com.hp.sh.expv3.pc.constant.TradingRoles;
import com.hp.sh.expv3.pc.module.account.api.request.AddMoneyRequest;
import com.hp.sh.expv3.pc.module.account.service.impl.PcAccountCoreService;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderDAO;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderTradeDAO;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.order.entity.PcOrderTrade;
import com.hp.sh.expv3.pc.module.position.dao.PcPositionDAO;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.symbol.dao.PcAccountSymbolDAO;
import com.hp.sh.expv3.pc.module.symbol.entity.PcAccountSymbol;
import com.hp.sh.expv3.pc.module.trade.entity.PcMatchedResult;
import com.hp.sh.expv3.pc.mq.msg.PcTradeMsg;
import com.hp.sh.expv3.pc.strategy.impl.AABBPositionStrategy;
import com.hp.sh.expv3.pc.strategy.impl.CommonOrderStrategy;
import com.hp.sh.expv3.pc.strategy.vo.TradeData;
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
	private PcAccountCoreService pcAccountCoreService;
	
	@Autowired
	private FeeCollectorSelector feeCollectorSelector;
	
	@Autowired
	private CommonOrderStrategy orderStrategy;
	
	@Autowired
	private AABBPositionStrategy positionStrategy;
	
	
	//处理成交订单
	public void handleTradeOrder(PcTradeMsg matchedVo){
		PcOrder order = this.pcOrderDAO.findById(matchedVo.getAccountId(), matchedVo.getOrderId());
		this.chekOrderStatus(order, matchedVo);
		
		PcPosition pcPosition = this.getCurrentPosition(matchedVo.getAccountId(), matchedVo.getAsset(), matchedVo.getSymbol(), order.getLongFlag());
		PcAccountSymbol as = pcAccountSymbolDAO.lockUserSymbol(order.getUserId(), order.getAsset(), order.getSymbol());
		
		TradeData tradeData = this.positionStrategy.getTradeData(order, matchedVo, pcPosition);
		PcOrderTrade pcOrderTrade = this.saveOrderTrade(matchedVo, order, tradeData);
		
		//如果仓位不存在则创建新仓位
		boolean isNewPos = false;
		if(pcPosition==null){
			pcPosition = this.newEmptyPostion(as.getUserId(), as.getAsset(), as.getSymbol(), order.getLongFlag(), order.getLeverage(), as.getMarginMode());
			isNewPos = true;
		}
		
		//仓位数量加减
		if(order.getCloseFlag() == OrderFlag.ACTION_OPEN){
			this.modOpenPos(pcPosition, tradeData);
		}else{
			this.modClosePos(pcPosition, tradeData);
		}
		//保存
		if(isNewPos){
			this.pcPositionDAO.save(pcPosition);
		}else{
			this.pcPositionDAO.update(pcPosition);
		}
		
		//修改订单状态：部分成交
		this.updateOrder(order, tradeData);
		
		//pc account
		this.transfer(order.getUserId(), pcOrderTrade.getId(), order.getAsset(), tradeData);
	}
	
	private void transfer(Long userId, Long orderTradeId, String asset, TradeData tradeData) {
		AddMoneyRequest request = new AddMoneyRequest();
		request.setAmount(tradeData.getOrderMargin().add(tradeData.getPnl()));
		request.setAsset(asset);
		request.setRemark("平仓");
		request.setTradeNo("CLOSE"+orderTradeId);
		request.setTradeType(PcAccountTradeType.ORDER_CANCEL);
		request.setUserId(userId);
		request.setAssociatedId(orderTradeId);
		this.pcAccountCoreService.add(request);
	}

	private void updateOrder(PcOrder order, TradeData tradeData){
		if(order.getCloseFlag() == OrderFlag.ACTION_OPEN){
	        order.setOrderMargin(order.getOrderMargin().subtract(tradeData.getOrderMargin()));
	        order.setOpenFee(order.getOpenFee().subtract(tradeData.getFee()));
		}
		order.setFeeCost(order.getFeeCost().add(tradeData.getFee()));
        order.setStatus(tradeData.isCompleted()?PcOrder.FILLED:PcOrder.PARTIALLY_FILLED);
        order.setActiveFlag(tradeData.isCompleted()?PcOrder.NO:PcOrder.YES);
		order.setFilledVolume(tradeData.getVolume());
		order.setModified(new Date());
		this.pcOrderDAO.update(order);
	}

	private PcOrderTrade saveOrderTrade(PcTradeMsg tradeMsg, PcOrder order, TradeData tradeData) {
		Date now = new Date();
		
		PcOrderTrade orderTrade = new PcOrderTrade();

		orderTrade.setId(null);
		orderTrade.setAsset(tradeMsg.getAsset());
		orderTrade.setSymbol(tradeMsg.getSymbol());
		orderTrade.setPrice(tradeMsg.getPrice());
		orderTrade.setVolume(tradeMsg.getNumber());
		orderTrade.setMakerFlag(tradeMsg.getMakerFlag());

		orderTrade.setFee(tradeData.getFee());
		orderTrade.setFeeRatio(tradeData.getFeeRatio());
		orderTrade.setPnl(tradeData.getPnl());
		
		orderTrade.setOrderId(order.getId());
		
		orderTrade.setTradeSn(tradeMsg.uniqueKey());
		orderTrade.setTradeTime(tradeMsg.getTradeTime());
		orderTrade.setUserId(order.getUserId());
		orderTrade.setCreated(now);
		orderTrade.setModified(now);
		
		orderTrade.setFeeCollectorId(feeCollectorSelector.getFeeCollectorId(order.getUserId(), order.getAsset(), order.getSymbol()));
		
		this.pcOrderTradeDAO.save(orderTrade);
		
		return orderTrade;
	}
	
	private PcPosition newEmptyPostion(long userId, String asset, String symbol, int longFlag, BigDecimal entryLeverage, int marginMode) {
		PcPosition pcPosition = new PcPosition();
		pcPosition.setUserId(userId);
		pcPosition.setAsset(asset);
		pcPosition.setSymbol(symbol);
		pcPosition.setLongFlag(longFlag);
		pcPosition.setMarginMode(marginMode);
		pcPosition.setEntryLeverage(entryLeverage);
		pcPosition.setLeverage(entryLeverage);
		pcPosition.setAutoAddFlag(IntBool.NO);
		pcPosition.setHoldRatio(marginRatioService.getHoldRatio(userId, asset, symbol, BigDecimal.ZERO));
		Date now = new Date();
		pcPosition.setCreated(now );
		pcPosition.setModified(now);
		//
		pcPosition.setVolume(BigDecimal.ZERO);
		pcPosition.setBaseValue(BigDecimal.ZERO);
		pcPosition.setPosMargin(BigDecimal.ZERO);
		pcPosition.setCloseFee(BigDecimal.ZERO);
		pcPosition.setMeanPrice(BigDecimal.ZERO);
		pcPosition.setInitMargin(BigDecimal.ZERO);
		pcPosition.setFeeCost(BigDecimal.ZERO);
		
		pcPosition.setRealisedPnl(BigDecimal.ZERO);
		
		pcPosition.setLiqPrice(BigDecimal.ZERO);
		
		pcPosition.setLiqMarkPrice(null);
		pcPosition.setLiqMarkTime(null);
		pcPosition.setLiqStatus(LiqStatus.NO);
		
		return pcPosition;
	}

	private void modOpenPos(PcPosition pcPosition, TradeData tradeData) {
		pcPosition.setVolume(pcPosition.getVolume().add(tradeData.getVolume()));
		pcPosition.setBaseValue(pcPosition.getBaseValue().add(tradeData.getBaseValue()));
		pcPosition.setPosMargin(pcPosition.getPosMargin().add(tradeData.getOrderMargin()));
		pcPosition.setCloseFee(pcPosition.getCloseFee().add(tradeData.getFee()));
		
		pcPosition.setMeanPrice(tradeData.getNewMeanPrice());
		pcPosition.setInitMargin(pcPosition.getInitMargin().add(tradeData.getOrderMargin()));
		pcPosition.setFeeCost(pcPosition.getFeeCost().add(tradeData.getFee()));
		
		pcPosition.setLiqPrice(tradeData.getLiqPrice());
		
	}

	private void modClosePos(PcPosition pcPosition, TradeData tradeData) {
		pcPosition.setVolume(pcPosition.getVolume().subtract(tradeData.getVolume()));
		pcPosition.setBaseValue(pcPosition.getBaseValue().subtract(tradeData.getBaseValue()));
		pcPosition.setPosMargin(pcPosition.getPosMargin().subtract(tradeData.getOrderMargin()));
		pcPosition.setCloseFee(pcPosition.getCloseFee().subtract(tradeData.getFee()));
		
		pcPosition.setMeanPrice(tradeData.getNewMeanPrice());
		pcPosition.setInitMargin(pcPosition.getInitMargin().subtract(tradeData.getOrderMargin()));
		pcPosition.setFeeCost(pcPosition.getFeeCost().subtract(tradeData.getFee()));
		
//		pcPosition.setLiqPrice(tradeData.getLiqPrice());
		
		pcPosition.setRealisedPnl(pcPosition.getRealisedPnl().add(tradeData.getPnl()));
	}
	
	/**
	 * 获取当前仓位，如果没有则创建一个
	 * @param userId
	 * @param asset
	 * @param symbol
	 * @param longFlag 多/空
	 * @return
	 */
	@Question("没有活动仓位字段")
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
	private void chekOrderStatus(PcOrder order, PcTradeMsg tradeMsg) {
		if(order==null){
			throw new ExException(CommonError.OBJ_DONT_EXIST);
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", order.getUserId());
		params.put("tradeSn", tradeMsg.uniqueKey());
		Long count = this.pcOrderTradeDAO.queryCount(params);
		if(count>0){
			throw new ExException(CommonError.OBJ_EXIST);
		}
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

	/**
	 * 处理成交
	 */
	public void handleMatchedResult(PcMatchedResult pcMatchedResult){
		//taker
		PcTradeMsg takerTradeVo = new PcTradeMsg();
		takerTradeVo.setMakerFlag(TradingRoles.TAKER);
		takerTradeVo.setAsset(pcMatchedResult.getAsset());
		takerTradeVo.setSymbol(pcMatchedResult.getSymbol());
		takerTradeVo.setPrice(pcMatchedResult.getPrice());
		takerTradeVo.setNumber(pcMatchedResult.getNumber());
		takerTradeVo.setTradeId(pcMatchedResult.getId());
		takerTradeVo.setTradeTime(pcMatchedResult.getTradeTime());
		
		takerTradeVo.setAccountId(pcMatchedResult.getTkAccountId());
		takerTradeVo.setMatchTxId(pcMatchedResult.getMatchTxId());
		takerTradeVo.setOrderId(pcMatchedResult.getTkOrderId());
		
		//maker
		PcTradeMsg makerTradeVo = new PcTradeMsg();
		makerTradeVo.setMakerFlag(TradingRoles.TAKER);
		makerTradeVo.setAsset(pcMatchedResult.getAsset());
		makerTradeVo.setSymbol(pcMatchedResult.getSymbol());
		makerTradeVo.setPrice(pcMatchedResult.getPrice());
		makerTradeVo.setNumber(pcMatchedResult.getNumber());
		makerTradeVo.setTradeId(pcMatchedResult.getId());
		makerTradeVo.setTradeTime(pcMatchedResult.getTradeTime());
		
		makerTradeVo.setAccountId(pcMatchedResult.getMkAccountId());
		makerTradeVo.setMatchTxId(pcMatchedResult.getMatchTxId());
		makerTradeVo.setOrderId(pcMatchedResult.getMkOrderId());
	
		//taker
		this.handleTradeOrder(takerTradeVo);
		
		//maker
		this.handleTradeOrder(makerTradeVo);
		
	}
	
}
