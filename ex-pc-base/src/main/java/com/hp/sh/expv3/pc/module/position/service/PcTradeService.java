package com.hp.sh.expv3.pc.module.position.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.base.exceptions.CommonError;
import com.hp.sh.expv3.commons.exception.ExSysException;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.pc.calc.CompFieldCalc;
import com.hp.sh.expv3.pc.component.FeeCollectorSelector;
import com.hp.sh.expv3.pc.component.FeeRatioService;
import com.hp.sh.expv3.pc.constant.LiqStatus;
import com.hp.sh.expv3.pc.constant.LogType;
import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.constant.OrderStatus;
import com.hp.sh.expv3.pc.constant.PcAccountTradeType;
import com.hp.sh.expv3.pc.constant.TradingRoles;
import com.hp.sh.expv3.pc.module.account.service.PcAccountCoreService;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderTradeDAO;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.order.entity.PcOrderTrade;
import com.hp.sh.expv3.pc.module.order.service.PcOrderQueryService;
import com.hp.sh.expv3.pc.module.order.service.PcOrderUpdateService;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.symbol.dao.PcAccountSymbolDAO;
import com.hp.sh.expv3.pc.module.symbol.entity.PcAccountSymbol;
import com.hp.sh.expv3.pc.module.trade.entity.PcMatchedResult;
import com.hp.sh.expv3.pc.msg.PcTradeMsg;
import com.hp.sh.expv3.pc.strategy.PositionStrategyContext;
import com.hp.sh.expv3.pc.strategy.vo.TradeResult;
import com.hp.sh.expv3.pc.vo.request.PcAddRequest;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.BigUtils;

@Service
@Transactional(rollbackFor=Exception.class)
public class PcTradeService {
	private static final Logger logger = LoggerFactory.getLogger(PcTradeService.class);

	@Autowired
	private PcPositionDataService positionDataService;
	
	@Autowired
	private PcOrderTradeDAO pcOrderTradeDAO;
	
	@Autowired
	private PcOrderUpdateService orderUpdateService;
	
	@Autowired
	private PcOrderQueryService orderQueryService;
	
	@Autowired
	private PcAccountSymbolDAO pcAccountSymbolDAO;

	@Autowired
	private FeeRatioService feeRatioService;
	
	@Autowired
	private PcAccountCoreService pcAccountCoreService;
	
	@Autowired
	private FeeCollectorSelector feeCollectorSelector;
	
	@Autowired
	private PositionStrategyContext positionStrategy;
    
	@Autowired
	private PcPositionMarginService positionMarginService;
	
    @Autowired
    private ApplicationEventPublisher publisher;
	
	//处理成交订单
    @LockIt(key="${trade.accountId}-${trade.asset}-${trade.symbol}")
	public void handleTradeOrder(PcTradeMsg trade){
		PcOrder order = this.orderQueryService.getOrder(trade.getAccountId(), trade.getOrderId());
		boolean yes = this.canTrade(order, trade);
		if(!yes){
			logger.error("成交已处理过了");
			return;
		}
		
		Long now  = DbDateUtils.now();
		
		PcPosition pcPosition = this.positionDataService.getCurrentPosition(trade.getAccountId(), trade.getAsset(), trade.getSymbol(), order.getLongFlag());
		PcAccountSymbol as = pcAccountSymbolDAO.lockUserSymbol(order.getUserId(), order.getAsset(), order.getSymbol());
		
		TradeResult tradeResult = this.positionStrategy.calcTradeResult(trade, order, pcPosition);
		
		////////// 仓位 ///////////
		//如果仓位不存在则创建新仓位
		boolean isNewPos = false;
		if(pcPosition==null){
			pcPosition = this.newEmptyPostion(as.getUserId(), as.getAsset(), as.getSymbol(), order.getLongFlag(), order.getLeverage(), as.getMarginMode(), order.getFaceValue());
			isNewPos = true;
		}
		
		//仓位数量加减
		if(order.getCloseFlag() == OrderFlag.ACTION_OPEN){
			this.modOpenPos(pcPosition, tradeResult);
		}else{
			this.modClosePos(pcPosition, tradeResult);
		}
		
		//修改维持保证金率
		BigDecimal holdRatio = this.feeRatioService.getHoldRatio(pcPosition.getUserId(), pcPosition.getAsset(), pcPosition.getSymbol(), pcPosition.getVolume());
		pcPosition.setHoldMarginRatio(holdRatio);
		
		//如果降档
		BigDecimal maxLeverage = this.feeRatioService.getMaxLeverage(pcPosition.getUserId(), pcPosition.getAsset(), pcPosition.getSymbol(), pcPosition.getVolume());
		if(BigUtils.gt(pcPosition.getLeverage(), maxLeverage)){
			positionMarginService.downLeverage(pcPosition, maxLeverage, now);
		}
		
		//保存
		if(isNewPos){
			pcPosition.setCreated(now);
			pcPosition.setModified(now);
			this.positionDataService.save(pcPosition);
		}else{
			pcPosition.setModified(now);
			this.positionDataService.update(pcPosition);
		}
		
		////////// 成交记录  ///////////
		PcOrderTrade pcOrderTrade = this.saveOrderTrade(trade, order, tradeResult, pcPosition.getId(), now);
		
		////////// 订单 ///////////
		
		//修改订单状态
		this.updateOrder4Trade(order, tradeResult, now);
		
		//////////pc_account ///////////
		if(order.getLiqFlag()==IntBool.YES){//强平委托
			return ;
		}
		
		if(order.getCloseFlag()==OrderFlag.ACTION_CLOSE){
			this.closeFeeToPcAccount(order.getUserId(), pcOrderTrade.getId(), order.getAsset(), tradeResult, order.getLongFlag());
		}else{
			if(BigUtils.ltZero(tradeResult.getFeeReceivable())){
				this.openFeeDiffToPcAccount(order.getUserId(), pcOrderTrade.getId(), order.getAsset(), tradeResult.getMakerFeeDiff());
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
		PcAddRequest request = new PcAddRequest();
		BigDecimal amount = tradeResult.getOrderMargin().add(tradeResult.getPnl()).subtract(tradeResult.getFeeReceivable());
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
		request.setRemark(String.format("平仓。保证金：%s,收益:%s,手续费：-%s", tradeResult.getOrderMargin(), tradeResult.getPnl(), tradeResult.getFeeReceivable()));
		request.setTradeNo("CLOSE-"+orderTradeId);
		request.setTradeType(IntBool.isTrue(longFlag)?PcAccountTradeType.ORDER_CLOSE_LONG:PcAccountTradeType.ORDER_CLOSE_SHORT);
		request.setAssociatedId(orderTradeId);
		this.pcAccountCoreService.add(request);
	}
	
	private void openFeeDiffToPcAccount(Long userId, Long orderTradeId, String asset, BigDecimal makerFeeDiff) {
		PcAddRequest request = new PcAddRequest();
		request.setAmount(makerFeeDiff);
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark(String.format("返还开仓手续费差额：%s", makerFeeDiff));
		request.setTradeNo("CLOSE-"+orderTradeId);
		request.setTradeType(PcAccountTradeType.RETURN_FEE_DIFF);
		request.setAssociatedId(orderTradeId);
		this.pcAccountCoreService.add(request);
	}

	private void updateOrder4Trade(PcOrder order, TradeResult tradeResult, Long now){
		if(order.getCloseFlag() == OrderFlag.ACTION_OPEN){
	        order.setOrderMargin(order.getOrderMargin().subtract(tradeResult.getOrderMargin()));
	        order.setOpenFee(order.getOpenFee().subtract(tradeResult.getFee()));
		}
		order.setFeeCost(order.getFeeCost().add(tradeResult.getFeeReceivable()));
		order.setFilledVolume(order.getFilledVolume().add(tradeResult.getVolume()));
        order.setStatus(tradeResult.getOrderCompleted()?OrderStatus.FILLED:OrderStatus.PARTIALLY_FILLED);
        order.setActiveFlag(tradeResult.getOrderCompleted()?PcOrder.NO:PcOrder.YES);
		order.setModified(now);
		this.orderUpdateService.updateOrder4Trad(order);
	}

	private PcOrderTrade saveOrderTrade(PcTradeMsg tradeMsg, PcOrder order, TradeResult tradeResult, Long posId, Long now) {
		PcOrderTrade orderTrade = new PcOrderTrade();

		orderTrade.setId(null);
		orderTrade.setAsset(tradeMsg.getAsset());
		orderTrade.setSymbol(tradeMsg.getSymbol());
		orderTrade.setVolume(tradeMsg.getNumber());
		orderTrade.setPrice(tradeMsg.getPrice());
		orderTrade.setMakerFlag(tradeMsg.getMakerFlag());

		orderTrade.setFee(tradeResult.getFeeReceivable());
		orderTrade.setFeeRatio(tradeResult.getFeeRatio());
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
		
		this.pcOrderTradeDAO.save(orderTrade);
		
		orderTrade.setLogType(this.getLogType(order.getCloseFlag(), order.getLongFlag()));
		
		publisher.publishEvent(orderTrade);
		
		return orderTrade;
	}
	
	private PcPosition newEmptyPostion(long userId, String asset, String symbol, int longFlag, BigDecimal entryLeverage, int marginMode, BigDecimal faceValue) {
		PcPosition pcPosition = new PcPosition();
		pcPosition.setUserId(userId);
		pcPosition.setAsset(asset);
		pcPosition.setSymbol(symbol);
		pcPosition.setLongFlag(longFlag);
		pcPosition.setMarginMode(marginMode);
		pcPosition.setEntryLeverage(entryLeverage);
		pcPosition.setLeverage(entryLeverage);
		pcPosition.setAutoAddFlag(IntBool.NO);
		pcPosition.setHoldMarginRatio(feeRatioService.getHoldRatio(userId, asset, symbol, BigDecimal.ZERO));

//		pcPosition.setCreated(now );
//		pcPosition.setModified(now);
		//
		pcPosition.setVolume(BigDecimal.ZERO);
		pcPosition.setBaseValue(BigDecimal.ZERO);
		pcPosition.setPosMargin(BigDecimal.ZERO);
		pcPosition.setCloseFee(BigDecimal.ZERO);
		pcPosition.setMeanPrice(BigDecimal.ZERO);
		pcPosition.setInitMargin(BigDecimal.ZERO);
		pcPosition.setFeeCost(BigDecimal.ZERO);
		
		pcPosition.setRealisedPnl(BigDecimal.ZERO);
		
		pcPosition.setLiqMarkPrice(null);
		pcPosition.setLiqMarkTime(null);
		pcPosition.setLiqStatus(LiqStatus.NON);
		
		pcPosition.setAccuVolume(BigDecimal.ZERO);
		pcPosition.setAccuBaseValue(BigDecimal.ZERO);
		
		pcPosition.setFaceValue(faceValue);
		
		return pcPosition;
	}

	private void modOpenPos(PcPosition pcPosition, TradeResult tradeResult) {
		pcPosition.setVolume(pcPosition.getVolume().add(tradeResult.getVolume()));
		pcPosition.setPosMargin(pcPosition.getPosMargin().add(tradeResult.getOrderMargin()));
		pcPosition.setCloseFee(pcPosition.getCloseFee().add(tradeResult.getFeeReceivable()));
		
		pcPosition.setMeanPrice(tradeResult.getNewPosMeanPrice());
		pcPosition.setInitMargin(pcPosition.getInitMargin().add(tradeResult.getOrderMargin()));
		pcPosition.setFeeCost(pcPosition.getFeeCost().add(tradeResult.getFeeReceivable()));
		
		pcPosition.setLiqPrice(tradeResult.getNewPosLiqPrice());
		
		pcPosition.setAccuVolume(pcPosition.getAccuVolume().add(tradeResult.getVolume()));
		
		pcPosition.setBaseValue(CompFieldCalc.calcBaseValue(pcPosition.getVolume(), pcPosition.getFaceValue(), pcPosition.getMeanPrice()));
		pcPosition.setAccuBaseValue(pcPosition.getAccuBaseValue().add(tradeResult.getBaseValue()));
		
	}

	private void modClosePos(PcPosition pcPosition, TradeResult tradeResult) {
		pcPosition.setVolume(pcPosition.getVolume().subtract(tradeResult.getVolume()));
		pcPosition.setPosMargin(pcPosition.getPosMargin().subtract(tradeResult.getOrderMargin()));
		pcPosition.setCloseFee(pcPosition.getCloseFee().subtract(tradeResult.getFeeReceivable()));
		
		pcPosition.setMeanPrice(tradeResult.getNewPosMeanPrice());
		pcPosition.setInitMargin(pcPosition.getInitMargin().subtract(tradeResult.getOrderMargin()));
		pcPosition.setFeeCost(pcPosition.getFeeCost().subtract(tradeResult.getFeeReceivable()));
		
		pcPosition.setLiqPrice(tradeResult.getNewPosLiqPrice());
		
		pcPosition.setBaseValue(CompFieldCalc.calcBaseValue(pcPosition.getVolume(), pcPosition.getFaceValue(), pcPosition.getMeanPrice()));
		
		pcPosition.setRealisedPnl(pcPosition.getRealisedPnl().add(tradeResult.getPnl()));

	}

	//检查订单状态
	private boolean canTrade(PcOrder order, PcTradeMsg tradeMsg) {
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
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", order.getUserId());
		params.put("tradeSn", tradeMsg.uniqueKey());
		Long count = this.pcOrderTradeDAO.queryCount(params);
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
	void handleMatchedResult(PcMatchedResult pcMatchedResult){
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
