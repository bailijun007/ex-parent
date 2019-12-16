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
import com.hp.sh.expv3.pc.component.FeeCollectorSelector;
import com.hp.sh.expv3.pc.component.FeeRatioService;
import com.hp.sh.expv3.pc.constant.LiqStatus;
import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.constant.PcAccountTradeType;
import com.hp.sh.expv3.pc.constant.TradingRoles;
import com.hp.sh.expv3.pc.module.account.service.PcAccountCoreService;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderDAO;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderTradeDAO;
import com.hp.sh.expv3.pc.module.order.entity.OrderStatus;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.order.entity.PcOrderTrade;
import com.hp.sh.expv3.pc.module.position.dao.PcPositionDAO;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.symbol.dao.PcAccountSymbolDAO;
import com.hp.sh.expv3.pc.module.symbol.entity.PcAccountSymbol;
import com.hp.sh.expv3.pc.module.trade.entity.PcMatchedResult;
import com.hp.sh.expv3.pc.mq.msg.PcTradeMsg;
import com.hp.sh.expv3.pc.strategy.aabb.AABBPositionStrategy;
import com.hp.sh.expv3.pc.strategy.vo.TradeResult;
import com.hp.sh.expv3.pc.vo.request.AddMoneyRequest;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.BigUtils;

@Service
@Transactional(rollbackFor=Exception.class)
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
	private FeeRatioService feeRatioService;
	
	@Autowired
	private PcAccountCoreService pcAccountCoreService;
	
	@Autowired
	private FeeCollectorSelector feeCollectorSelector;
	
	@Autowired
	private AABBPositionStrategy positionStrategy;
	
	//处理成交订单
	public void handleTradeOrder(PcTradeMsg matchedVo){
		PcOrder order = this.pcOrderDAO.findById(matchedVo.getAccountId(), matchedVo.getOrderId());
		this.chekOrderStatus(order, matchedVo);
		
		PcPosition pcPosition = this.getCurrentPosition(matchedVo.getAccountId(), matchedVo.getAsset(), matchedVo.getSymbol(), order.getLongFlag());
		PcAccountSymbol as = pcAccountSymbolDAO.lockUserSymbol(order.getUserId(), order.getAsset(), order.getSymbol());
		
		TradeResult tradeResult = this.positionStrategy.getTradeResult(order, matchedVo, pcPosition);
		PcOrderTrade pcOrderTrade = this.saveOrderTrade(matchedVo, order, tradeResult);
		
		//如果仓位不存在则创建新仓位
		boolean isNewPos = false;
		if(pcPosition==null){
			pcPosition = this.newEmptyPostion(as.getUserId(), as.getAsset(), as.getSymbol(), order.getLongFlag(), order.getLeverage(), as.getMarginMode());
			isNewPos = true;
		}
		
		//仓位数量加减
		if(order.getCloseFlag() == OrderFlag.ACTION_OPEN){
			this.modOpenPos(pcPosition, tradeResult);
		}else{
			this.modClosePos(pcPosition, tradeResult);
		}
		//保存
		if(isNewPos){
			this.pcPositionDAO.save(pcPosition);
		}else{
			this.pcPositionDAO.update(pcPosition);
		}
		
		//修改订单状态
		this.updateOrder(order, tradeResult);
		
		//pc account
		if(order.getCloseFlag()==OrderFlag.ACTION_CLOSE){
			this.closeFeeToPcAccount(order.getUserId(), pcOrderTrade.getId(), order.getAsset(), tradeResult, order.getLongFlag());
		}else{
			if(BigUtils.ltZero(tradeResult.getFeeReceivable())){
				this.openFeeDiffToPcAccount(order.getUserId(), pcOrderTrade.getId(), order.getAsset(), tradeResult);
			}
		}
	}
	
	private void closeFeeToPcAccount(Long userId, Long orderTradeId, String asset, TradeResult tradeResult, int longFlag) {
		AddMoneyRequest request = new AddMoneyRequest();
		request.setAmount(tradeResult.getOrderMargin().add(tradeResult.getPnl()).subtract(tradeResult.getFeeReceivable()));
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark(String.format("平仓,保证金：%d,收益:%s,手续费：%d", tradeResult.getOrderMargin(), tradeResult.getPnl(), tradeResult.getFeeReceivable()));
		request.setTradeNo("CLOSE-"+orderTradeId);
		request.setTradeType(IntBool.isTrue(longFlag)?PcAccountTradeType.ORDER_CLOSE_LONG:PcAccountTradeType.ORDER_CLOSE_SHORT);
		request.setAssociatedId(orderTradeId);
		this.pcAccountCoreService.add(request);
	}
	
	private void openFeeDiffToPcAccount(Long userId, Long orderTradeId, String asset, TradeResult tradeData) {
		AddMoneyRequest request = new AddMoneyRequest();
		request.setAmount(tradeData.getMakerFeeDiff());
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark(String.format("返还开仓手续费差额：%d", tradeData.getMakerFeeDiff()));
		request.setTradeNo("CLOSE-"+orderTradeId);
		request.setTradeType(PcAccountTradeType.RETURN_FEE_DIFF);
		request.setAssociatedId(orderTradeId);
		this.pcAccountCoreService.add(request);
	}

	private void updateOrder(PcOrder order, TradeResult tradeData){
		if(order.getCloseFlag() == OrderFlag.ACTION_OPEN){
	        order.setOrderMargin(order.getOrderMargin().subtract(tradeData.getOrderMargin()));
	        order.setOpenFee(order.getOpenFee().subtract(tradeData.getFee()));
		}
		order.setFeeCost(order.getFeeCost().add(tradeData.getFeeReceivable()));
        order.setStatus(tradeData.isOrderCompleted()?OrderStatus.FILLED:OrderStatus.PARTIALLY_FILLED);
        order.setActiveFlag(tradeData.isOrderCompleted()?PcOrder.NO:PcOrder.YES);
		order.setFilledVolume(tradeData.getVolume());
		order.setModified(new Date());
		this.pcOrderDAO.update(order);
	}

	private PcOrderTrade saveOrderTrade(PcTradeMsg tradeMsg, PcOrder order, TradeResult tradeData) {
		Date now = new Date();
		
		PcOrderTrade orderTrade = new PcOrderTrade();

		orderTrade.setId(null);
		orderTrade.setAsset(tradeMsg.getAsset());
		orderTrade.setSymbol(tradeMsg.getSymbol());
		orderTrade.setPrice(tradeMsg.getPrice());
		orderTrade.setVolume(tradeMsg.getNumber());
		orderTrade.setMakerFlag(tradeMsg.getMakerFlag());

		orderTrade.setFee(tradeData.getFeeReceivable());
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
		pcPosition.setHoldRatio(feeRatioService.getHoldRatio(userId, asset, symbol, BigDecimal.ZERO));
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
		
		pcPosition.setAccuVolume(BigDecimal.ZERO);
		pcPosition.setAccuBaseValue(BigDecimal.ZERO);
		
		return pcPosition;
	}

	private void modOpenPos(PcPosition pcPosition, TradeResult tradeResult) {
		pcPosition.setVolume(pcPosition.getVolume().add(tradeResult.getVolume()));
		pcPosition.setBaseValue(pcPosition.getBaseValue().add(tradeResult.getBaseValue()));
		pcPosition.setPosMargin(pcPosition.getPosMargin().add(tradeResult.getOrderMargin()));
		pcPosition.setCloseFee(pcPosition.getCloseFee().add(tradeResult.getFeeReceivable()));
		
		pcPosition.setMeanPrice(tradeResult.getNewMeanPrice());
		pcPosition.setInitMargin(pcPosition.getInitMargin().add(tradeResult.getOrderMargin()));
		pcPosition.setFeeCost(pcPosition.getFeeCost().add(tradeResult.getFeeReceivable()));
		
		pcPosition.setLiqPrice(tradeResult.getLiqPrice());
		
		//
		pcPosition.setAccuVolume(pcPosition.getAccuBaseValue().add(tradeResult.getVolume()));
		pcPosition.setAccuBaseValue(pcPosition.getAccuBaseValue().add(tradeResult.getBaseValue()));
		
	}

	private void modClosePos(PcPosition pcPosition, TradeResult tradeData) {
		pcPosition.setVolume(pcPosition.getVolume().subtract(tradeData.getVolume()));
		pcPosition.setBaseValue(pcPosition.getBaseValue().subtract(tradeData.getBaseValue()));
		pcPosition.setPosMargin(pcPosition.getPosMargin().subtract(tradeData.getOrderMargin()));
		pcPosition.setCloseFee(pcPosition.getCloseFee().subtract(tradeData.getFeeReceivable()));
		
		pcPosition.setMeanPrice(tradeData.getNewMeanPrice());
		pcPosition.setInitMargin(pcPosition.getInitMargin().subtract(tradeData.getOrderMargin()));
		pcPosition.setFeeCost(pcPosition.getFeeCost().subtract(tradeData.getFeeReceivable()));
		
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
	public PcPosition getCurrentPosition(Long userId, String asset, String symbol, int longFlag){
		PcPosition pos = this.pcPositionDAO.getActivePos(userId, asset, symbol, longFlag);
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
	
	private void synchCollector(Long tradeOrderId, Long feeCollectorId, BigDecimal fee){
		
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
