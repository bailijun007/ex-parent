package com.hp.sh.expv3.bb.module.position.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.bb.calc.CompFieldCalc;
import com.hp.sh.expv3.bb.component.FeeCollectorSelector;
import com.hp.sh.expv3.bb.component.FeeRatioService;
import com.hp.sh.expv3.bb.constant.LiqStatus;
import com.hp.sh.expv3.bb.constant.LogType;
import com.hp.sh.expv3.bb.constant.OrderFlag;
import com.hp.sh.expv3.bb.constant.OrderStatus;
import com.hp.sh.expv3.bb.constant.BBAccountTradeType;
import com.hp.sh.expv3.bb.constant.TradingRoles;
import com.hp.sh.expv3.bb.module.account.service.BBAccountCoreService;
import com.hp.sh.expv3.bb.module.order.dao.BBOrderTradeDAO;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderTrade;
import com.hp.sh.expv3.bb.module.order.service.BBOrderQueryService;
import com.hp.sh.expv3.bb.module.order.service.BBOrderUpdateService;
import com.hp.sh.expv3.bb.module.position.entity.BBPosition;
import com.hp.sh.expv3.bb.module.symbol.dao.BBAccountSymbolDAO;
import com.hp.sh.expv3.bb.module.symbol.entity.BBAccountSymbol;
import com.hp.sh.expv3.bb.module.trade.entity.BBMatchedResult;
import com.hp.sh.expv3.bb.msg.BBTradeMsg;
import com.hp.sh.expv3.bb.strategy.PositionStrategyContext;
import com.hp.sh.expv3.bb.strategy.vo.TradeResult;
import com.hp.sh.expv3.bb.vo.request.BBAddRequest;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.BigUtils;

@Service
@Transactional(rollbackFor=Exception.class)
public class BBTradeService {
	private static final Logger logger = LoggerFactory.getLogger(BBTradeService.class);

	@Autowired
	private BBPositionDataService positionDataService;
	
	@Autowired
	private BBOrderTradeDAO bBOrderTradeDAO;
	
	@Autowired
	private BBOrderUpdateService orderUpdateService;
	
	@Autowired
	private BBOrderQueryService orderQueryService;
	
	@Autowired
	private BBAccountSymbolDAO bBAccountSymbolDAO;

	@Autowired
	private FeeRatioService feeRatioService;
	
	@Autowired
	private BBAccountCoreService bBAccountCoreService;
	
	@Autowired
	private FeeCollectorSelector feeCollectorSelector;
	
	@Autowired
	private PositionStrategyContext positionStrategy;
    
	@Autowired
	private BBPositionMarginService positionMarginService;
	
    @Autowired
    private ApplicationEventPublisher publisher;
	
	//处理成交订单
    @LockIt(key="${trade.accountId}-${trade.asset}-${trade.symbol}")
	public void handleTradeOrder(BBTradeMsg trade){
		BBOrder order = this.orderQueryService.getOrder(trade.getAccountId(), trade.getOrderId());
		boolean yes = this.canTrade(order, trade);
		if(!yes){
			logger.error("成交已处理过了");
			return;
		}
		
		Long now = DbDateUtils.now();
		
		BBPosition bBPosition = this.positionDataService.getCurrentPosition(trade.getAccountId(), trade.getAsset(), trade.getSymbol(), order.getLongFlag());
		BBAccountSymbol as = bBAccountSymbolDAO.lockUserSymbol(order.getUserId(), order.getAsset(), order.getSymbol());
		
		TradeResult tradeResult = this.positionStrategy.calcTradeResult(trade, order, bBPosition);
		
		////////// 仓位 ///////////
		//如果仓位不存在则创建新仓位
		boolean isNewPos = false;
		if(bBPosition==null){
			bBPosition = this.newEmptyPostion(as.getUserId(), as.getAsset(), as.getSymbol(), order.getLongFlag(), order.getLeverage(), as.getMarginMode(), order.getFaceValue());
			isNewPos = true;
		}
		
		//仓位数量加减
		if(order.getCloseFlag() == OrderFlag.ACTION_OPEN){
			this.modOpenPos(bBPosition, tradeResult);
		}else{
			this.modClosePos(bBPosition, tradeResult);
		}
		
		//修改维持保证金率
		BigDecimal holdRatio = this.feeRatioService.getHoldRatio(bBPosition.getUserId(), bBPosition.getAsset(), bBPosition.getSymbol(), bBPosition.getVolume());
		bBPosition.setHoldMarginRatio(holdRatio);
		
		//如果升档
		BigDecimal maxLeverage = this.feeRatioService.getMaxLeverage(bBPosition.getUserId(), bBPosition.getAsset(), bBPosition.getSymbol(), bBPosition.getVolume());
		if(BigUtils.gt(bBPosition.getLeverage(), maxLeverage)){
			positionMarginService.downLeverage(bBPosition, maxLeverage, now);
		}
		
		//保存
		if(isNewPos){
			bBPosition.setCreated(now);
			bBPosition.setModified(now);
			this.positionDataService.save(bBPosition);
		}else{
			bBPosition.setModified(now);
			this.positionDataService.update(bBPosition);
		}
		
		////////// 成交记录  ///////////
		BBOrderTrade bBOrderTrade = this.saveOrderTrade(trade, order, tradeResult, bBPosition.getId(), now);
		
		////////// 订单 ///////////
		
		//修改订单状态
		this.updateOrder4Trade(order, tradeResult, now);
		
		//////////pc_account ///////////
		if(order.getLiqFlag()==IntBool.YES){//强平委托
			return ;
		}
		
		if(order.getCloseFlag()==OrderFlag.ACTION_CLOSE){
			this.closeFeeToPcAccount(order.getUserId(), bBOrderTrade.getId(), order.getAsset(), tradeResult, order.getLongFlag());
		}else{
			if(BigUtils.ltZero(tradeResult.getReceivableFee())){
				this.openFeeDiffToPcAccount(order.getUserId(), bBOrderTrade.getId(), order.getAsset(), tradeResult.getMakerFeeDiff());
			}
		}
		
	}
    
    private int getLogType(int closeFlag, int longFlag){
		if(IntBool.isFalse(closeFlag)){
			return IntBool.isTrue(longFlag)?LogType.TYPE_TRAD_OPEN_LONG:LogType.TYPE_TRAD_OPEN_SHORT;
		}else{
			return IntBool.isTrue(longFlag)?LogType.TYPE_TRAD_CLOSE_LONG:LogType.TYPE_TRAD_CLOSE_SHORT;
		}
	}
	
	private void closeFeeToPcAccount(Long userId, Long orderTradeId, String asset, TradeResult tradeResult, int longFlag) {
		BBAddRequest request = new BBAddRequest();
		BigDecimal amount = tradeResult.getOrderMargin().add(tradeResult.getPnl()).subtract(tradeResult.getReceivableFee());
		if(BigUtils.isZero(amount)){
			return;
		}
		if(BigUtils.ltZero(amount)){
			logger.error("穿仓。。。。");
			return;
		}
		request.setAmount(amount);
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark(String.format("平仓。保证金：%s,收益:%s,手续费：-%s", tradeResult.getOrderMargin(), tradeResult.getPnl(), tradeResult.getReceivableFee()));
		request.setTradeNo("CLOSE-"+orderTradeId);
		request.setTradeType(IntBool.isTrue(longFlag)?BBAccountTradeType.ORDER_CLOSE_LONG:BBAccountTradeType.ORDER_CLOSE_SHORT);
		request.setAssociatedId(orderTradeId);
		this.bBAccountCoreService.add(request);
	}
	
	private void openFeeDiffToPcAccount(Long userId, Long orderTradeId, String asset, BigDecimal makerFeeDiff) {
		BBAddRequest request = new BBAddRequest();
		request.setAmount(makerFeeDiff);
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark(String.format("返还开仓手续费差额：%s", makerFeeDiff));
		request.setTradeNo("CLOSE-"+orderTradeId);
		request.setTradeType(BBAccountTradeType.RETURN_FEE_DIFF);
		request.setAssociatedId(orderTradeId);
		this.bBAccountCoreService.add(request);
	}

	private void updateOrder4Trade(BBOrder order, TradeResult tradeResult, Long now){
		if(order.getCloseFlag() == OrderFlag.ACTION_OPEN){
	        order.setOrderMargin(order.getOrderMargin().subtract(tradeResult.getOrderMargin()));
	        order.setOpenFee(order.getOpenFee().subtract(tradeResult.getFee()));
		}
		order.setFeeCost(order.getFeeCost().add(tradeResult.getReceivableFee()));
		order.setFilledVolume(order.getFilledVolume().add(tradeResult.getVolume()));
        order.setStatus(tradeResult.getOrderCompleted()?OrderStatus.FILLED:OrderStatus.PARTIALLY_FILLED);
        order.setActiveFlag(tradeResult.getOrderCompleted()?BBOrder.NO:BBOrder.YES);
		order.setModified(now);
		this.orderUpdateService.updateOrder4Trad(order);
	}

	private BBOrderTrade saveOrderTrade(BBTradeMsg tradeMsg, BBOrder order, TradeResult tradeResult, Long posId, Long now) {
		BBOrderTrade orderTrade = new BBOrderTrade();

		orderTrade.setId(null);
		orderTrade.setAsset(tradeMsg.getAsset());
		orderTrade.setSymbol(tradeMsg.getSymbol());
		orderTrade.setVolume(tradeMsg.getNumber());
		orderTrade.setPrice(tradeMsg.getPrice());
		orderTrade.setMakerFlag(tradeMsg.getMakerFlag());

		orderTrade.setFee(tradeResult.getReceivableFee());
		orderTrade.setFeeRatio(tradeResult.getReceivableFeeRatio());
		orderTrade.setPnl(tradeResult.getPnl());
		
		orderTrade.setUserId(order.getUserId());
		orderTrade.setCreated(now);
		orderTrade.setModified(now);

		orderTrade.setTradeSn(tradeMsg.uniqueKey());
		orderTrade.setTradeId(tradeMsg.getTradeId());
		orderTrade.setTradeTime(tradeMsg.getTradeTime());
		
		orderTrade.setPosId(posId);
		orderTrade.setOrderId(order.getId());
		
		orderTrade.setFeeCollectorId(feeCollectorSelector.getFeeCollectorId(order.getUserId(), order.getAsset(), order.getSymbol()));
		
		orderTrade.setRemainVolume(order.getVolume().subtract(order.getFilledVolume()).subtract(tradeResult.getVolume()));
		
		orderTrade.setMatchTxId(tradeMsg.getMatchTxId());
		
		this.bBOrderTradeDAO.save(orderTrade);
		
		orderTrade.setLogType(this.getLogType(order.getCloseFlag(), order.getLongFlag()));
		
		publisher.publishEvent(orderTrade);
		
		return orderTrade;
	}
	
	private BBPosition newEmptyPostion(long userId, String asset, String symbol, int longFlag, BigDecimal entryLeverage, int marginMode, BigDecimal faceValue) {
		BBPosition bBPosition = new BBPosition();
		bBPosition.setUserId(userId);
		bBPosition.setAsset(asset);
		bBPosition.setSymbol(symbol);
		bBPosition.setLongFlag(longFlag);
		bBPosition.setMarginMode(marginMode);
		bBPosition.setEntryLeverage(entryLeverage);
		bBPosition.setLeverage(entryLeverage);
		bBPosition.setAutoAddFlag(IntBool.NO);
		bBPosition.setHoldMarginRatio(feeRatioService.getHoldRatio(userId, asset, symbol, BigDecimal.ZERO));

//		pcPosition.setCreated(now );
//		pcPosition.setModified(now);
		//
		bBPosition.setVolume(BigDecimal.ZERO);
		bBPosition.setBaseValue(BigDecimal.ZERO);
		bBPosition.setPosMargin(BigDecimal.ZERO);
		bBPosition.setCloseFee(BigDecimal.ZERO);
		bBPosition.setMeanPrice(BigDecimal.ZERO);
		bBPosition.setInitMargin(BigDecimal.ZERO);
		bBPosition.setFeeCost(BigDecimal.ZERO);
		
		bBPosition.setRealisedPnl(BigDecimal.ZERO);
		
		bBPosition.setLiqMarkPrice(null);
		bBPosition.setLiqMarkTime(null);
		bBPosition.setLiqStatus(LiqStatus.NON);
		
		bBPosition.setAccuVolume(BigDecimal.ZERO);
		bBPosition.setAccuBaseValue(BigDecimal.ZERO);
		
		bBPosition.setFaceValue(faceValue);
		
		return bBPosition;
	}

	private void modOpenPos(BBPosition bBPosition, TradeResult tradeResult) {
		bBPosition.setVolume(bBPosition.getVolume().add(tradeResult.getVolume()));
		bBPosition.setPosMargin(bBPosition.getPosMargin().add(tradeResult.getOrderMargin()));
		bBPosition.setCloseFee(bBPosition.getCloseFee().add(tradeResult.getReceivableFee()));
		
		bBPosition.setMeanPrice(tradeResult.getNewPosMeanPrice());
		bBPosition.setInitMargin(bBPosition.getInitMargin().add(tradeResult.getOrderMargin()));
		bBPosition.setFeeCost(bBPosition.getFeeCost().add(tradeResult.getReceivableFee()));
		
		bBPosition.setLiqPrice(tradeResult.getNewPosLiqPrice());
		
		bBPosition.setAccuVolume(bBPosition.getAccuVolume().add(tradeResult.getVolume()));
		
		bBPosition.setBaseValue(CompFieldCalc.calcBaseValue(bBPosition.getVolume(), bBPosition.getFaceValue(), bBPosition.getMeanPrice()));
		bBPosition.setAccuBaseValue(bBPosition.getAccuBaseValue().add(tradeResult.getBaseValue()));
		
	}

	private void modClosePos(BBPosition bBPosition, TradeResult tradeResult) {
		bBPosition.setVolume(bBPosition.getVolume().subtract(tradeResult.getVolume()));
		bBPosition.setPosMargin(bBPosition.getPosMargin().subtract(tradeResult.getOrderMargin()));
		bBPosition.setCloseFee(bBPosition.getCloseFee().subtract(tradeResult.getReceivableFee()));
		
		bBPosition.setMeanPrice(tradeResult.getNewPosMeanPrice());
		bBPosition.setInitMargin(bBPosition.getInitMargin().subtract(tradeResult.getOrderMargin()));
		bBPosition.setFeeCost(bBPosition.getFeeCost().subtract(tradeResult.getReceivableFee()));
		
		bBPosition.setLiqPrice(tradeResult.getNewPosLiqPrice());
		
		bBPosition.setBaseValue(CompFieldCalc.calcBaseValue(bBPosition.getVolume(), bBPosition.getFaceValue(), bBPosition.getMeanPrice()));
		
		bBPosition.setRealisedPnl(bBPosition.getRealisedPnl().add(tradeResult.getPnl()));

	}

	//检查订单状态
	private boolean canTrade(BBOrder order, BBTradeMsg tradeMsg) {
		if(order==null){
			logger.error("成交订单不存在：orderId={}", tradeMsg.getOrderId());
//			throw new ExSysException(CommonError.OBJ_DONT_EXIST);
			return false;
		}
		
		BigDecimal remainVol = order.getVolume().subtract(order.getFilledVolume());
		if(BigUtils.gt(tradeMsg.getNumber(), remainVol)){
			return false;
		}
		
		//检查重复请求
		Long count = this.bBOrderTradeDAO.exist(order.getUserId(), tradeMsg.uniqueKey());
		if(count>0){
			return false;
		}
		return true;
	}
	
	private void synchCollector(Long tradeOrderId, Long feeCollectorId, BigDecimal fee){
		
	}

	/**
	 * 处理成交
	 */
	void handleMatchedResult(BBMatchedResult bBMatchedResult){
		//taker
		BBTradeMsg takerTradeVo = new BBTradeMsg();
		takerTradeVo.setMakerFlag(TradingRoles.TAKER);
		takerTradeVo.setAsset(bBMatchedResult.getAsset());
		takerTradeVo.setSymbol(bBMatchedResult.getSymbol());
		takerTradeVo.setPrice(bBMatchedResult.getPrice());
		takerTradeVo.setNumber(bBMatchedResult.getNumber());
		takerTradeVo.setTradeId(bBMatchedResult.getId());
		takerTradeVo.setTradeTime(bBMatchedResult.getTradeTime());
		
		takerTradeVo.setAccountId(bBMatchedResult.getTkAccountId());
		takerTradeVo.setMatchTxId(bBMatchedResult.getMatchTxId());
		takerTradeVo.setOrderId(bBMatchedResult.getTkOrderId());
		
		//maker
		BBTradeMsg makerTradeVo = new BBTradeMsg();
		makerTradeVo.setMakerFlag(TradingRoles.TAKER);
		makerTradeVo.setAsset(bBMatchedResult.getAsset());
		makerTradeVo.setSymbol(bBMatchedResult.getSymbol());
		makerTradeVo.setPrice(bBMatchedResult.getPrice());
		makerTradeVo.setNumber(bBMatchedResult.getNumber());
		makerTradeVo.setTradeId(bBMatchedResult.getId());
		makerTradeVo.setTradeTime(bBMatchedResult.getTradeTime());
		
		makerTradeVo.setAccountId(bBMatchedResult.getMkAccountId());
		makerTradeVo.setMatchTxId(bBMatchedResult.getMatchTxId());
		makerTradeVo.setOrderId(bBMatchedResult.getMkOrderId());
	
		//taker
		this.handleTradeOrder(takerTradeVo);
		
		//maker
		this.handleTradeOrder(makerTradeVo);
		
	}
	
}
