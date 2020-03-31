package com.hp.sh.expv3.pc.module.position.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.pc.component.FeeCollectorSelector;
import com.hp.sh.expv3.pc.component.FeeRatioService;
import com.hp.sh.expv3.pc.constant.LiqStatus;
import com.hp.sh.expv3.pc.constant.LogType;
import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.constant.OrderStatus;
import com.hp.sh.expv3.pc.constant.PcAccountTradeType;
import com.hp.sh.expv3.pc.constant.TradingRoles;
import com.hp.sh.expv3.pc.module.account.service.PcAccountCoreService;
import com.hp.sh.expv3.pc.module.collector.service.PcCollectorCoreService;
import com.hp.sh.expv3.pc.module.liq.dao.PcLiqRecordDAO;
import com.hp.sh.expv3.pc.module.liq.entity.PcLiqRecord;
import com.hp.sh.expv3.pc.module.liq.entity.LiqRecordStatus;
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
import com.hp.sh.expv3.pc.strategy.PcStrategyContext;
import com.hp.sh.expv3.pc.strategy.vo.TradeResult;
import com.hp.sh.expv3.pc.vo.request.CollectorAddRequest;
import com.hp.sh.expv3.pc.vo.request.CollectorCutRequest;
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
	private PcOrderTradeDAO orderTradeDAO;
	
	@Autowired
	private PcOrderUpdateService orderUpdateService;
	
	@Autowired
	private PcOrderQueryService orderQueryService;
	
	@Autowired
	private PcAccountSymbolDAO accountSymbolDAO;

	@Autowired
	private FeeRatioService feeRatioService;
	
	@Autowired
	private PcAccountCoreService accountCoreService;
	
	@Autowired
	private FeeCollectorSelector feeCollectorSelector;
	
	@Autowired
	private PcStrategyContext positionStrategy;
    
	@Autowired
	private PcPositionMarginService positionMarginService;
	
    @Autowired
	private PcLiqRecordDAO pcLiqRecordDAO;

	@Autowired
    private ApplicationEventPublisher publisher;

	
	//处理成交订单
    @LockIt(key="${trade.accountId}-${trade.asset}-${trade.symbol}")
	public void handleTradeOrder(PcTradeMsg trade){
		PcOrder order = this.orderQueryService.getOrder(trade.getAccountId(), trade.getOrderId());
		boolean yes = this.canTrade(order, trade);
		if(!yes){
			logger.warn("成交已处理过了,trade={}", trade);
			return;
		}
		
		if(order.getCloseFlag()==OrderFlag.ACTION_OPEN){
			this.handleTradeOpenOrder(order, trade);
		}else{
			if(order.getLiqFlag()==IntBool.YES){//强平委托
				this.handleTradeLiqOrder(order, trade);
			}else{
				this.handleTradeCloseOrder(order, trade);
			}
		}
		
	}
    
    protected void handleTradeLiqOrder(PcOrder order, PcTradeMsg trade) {
		Long now = DbDateUtils.now();
		/*  1、修改订单数据和订单状态  */
		order.setFilledVolume(order.getFilledVolume().add(trade.getNumber()));
		boolean com = BigUtils.isZero(order.getVolume().subtract(order.getFilledVolume()).subtract(order.getCancelVolume()));
        order.setStatus(com?OrderStatus.FILLED:OrderStatus.PARTIALLY_FILLED);
//        order.setActiveFlag(com?PcOrder.NO:PcOrder.YES);
		order.setModified(now);
		this.orderUpdateService.updateOrder4Trad(order);
		
		/* 2、保险基金 */
		BigDecimal pnl = positionStrategy.calcPnl(order.getAsset(), order.getSymbol(), order.getLongFlag(), order.getFaceValue(), trade.getNumber(), order.getPrice(), trade.getPrice());
		
		if(BigUtils.ltZero(pnl)){
			throw new RuntimeException("破产价委托，盈余:{}" + pnl);
		}
		
		/* 3、强平记录 */
		String _rid = order.getClientOrderId().split("-")[1];
		Long recordId = Long.parseLong(_rid);
		PcLiqRecord lqRecord = pcLiqRecordDAO.findById(order.getUserId(), recordId);
		lqRecord.setFilledVolume(trade.getNumber());
		lqRecord.setPnl(lqRecord.getPnl()==null?pnl:lqRecord.getPnl().add(pnl));
		lqRecord.setModified(now);
		lqRecord.setStatus(LiqRecordStatus.BANKRUPT_ORDER_TRADE);
		this.pcLiqRecordDAO.update(lqRecord);
		
	}

    //处理成交订单
	protected void handleTradeOpenOrder(PcOrder order, PcTradeMsg trade){
		Long now = DbDateUtils.now();
		
		PcPosition pcPosition = this.positionDataService.getCurrentPosition(trade.getAccountId(), trade.getAsset(), trade.getSymbol(), order.getLongFlag());
		
		TradeResult tradeResult = this.positionStrategy.calcTradeResult(trade, order, pcPosition);
		
		/*  1、修改仓位数据  */
		pcPosition = this.modPositon(pcPosition, order, tradeResult, now);
		
		/*  2、修改订单数据和订单状态  */
		this.updateOrder4Trade(order, tradeResult, now);
		
		/* 3、生成成交流水 */
		PcOrderTrade pcOrderTrade = this.saveOrderTrade(trade, order, tradeResult, pcPosition.getId(), now);
		
		/* 4、返开仓手续费 */
		if(tradeResult.getOrderCompleted()){
			if(BigUtils.gtZero(order.getOpenFee())){
				this.returnRemainOpenfee(pcOrderTrade.getUserId(), pcOrderTrade.getId(), pcOrderTrade.getAsset(), order.getOpenFee());
			}else if(BigUtils.ltZero(order.getOpenFee())){
				logger.error("剩余手续费小于0,{}", pcOrderTrade.getId());
			}
		}
		
	}
    
    //处理成交订单
	protected void handleTradeCloseOrder(PcOrder order, PcTradeMsg trade){
		Long now = DbDateUtils.now();
		
		PcPosition pcPosition = this.positionDataService.getCurrentPosition(trade.getAccountId(), trade.getAsset(), trade.getSymbol(), order.getLongFlag());
		
		TradeResult tradeResult = this.positionStrategy.calcTradeResult(trade, order, pcPosition);
		
		/*  1、修改仓位数据  */
		pcPosition = this.modPositon(pcPosition, order, tradeResult, now);
		
		/*  2、修改订单数据和订单状态  */
		this.updateOrder4Trade(order, tradeResult, now);
		
		/* 3、生成成交流水 */
		PcOrderTrade pcOrderTrade = this.saveOrderTrade(trade, order, tradeResult, pcPosition.getId(), now);

		/*  4、返还保证金和手续费  */
		//仓位全平
		BigDecimal posCloseFee = BigDecimal.ZERO;
		if(BigUtils.isZero(pcPosition.getVolume())){
			posCloseFee = pcPosition.getCloseFee();
		}
		BigDecimal amount = tradeResult.getOrderMargin().add(tradeResult.getPnl()).subtract(tradeResult.getFee()).add(posCloseFee);
		String remark = String.format("平仓。保证金：%s,收益:%s,手续费：-%s,仓位剩余手续费：%s", tradeResult.getOrderMargin(), tradeResult.getPnl(), tradeResult.getFee(), posCloseFee);
		this.closeMarginToPcAccount(order.getUserId(), pcOrderTrade.getId(), order.getAsset(), amount, order.getLongFlag(), remark);
	}

	private PcPosition modPositon(PcPosition pcPosition, PcOrder order, TradeResult tradeResult, Long now){
		////////// 仓位 ///////////
		//如果仓位不存在则创建新仓位
		boolean isNewPos = false;
		if(pcPosition==null){
			PcAccountSymbol as = accountSymbolDAO.lockUserSymbol(order.getUserId(), order.getAsset(), order.getSymbol());
			pcPosition = this.newEmptyPostion(as.getUserId(), as.getAsset(), as.getSymbol(), order.getLongFlag(), order.getLeverage(), as.getMarginMode(), order.getFaceValue());
			isNewPos = true;
		}
		
		/*  1、修改仓位数据  */
		if(order.getCloseFlag() == OrderFlag.ACTION_OPEN){
			this.openPositon(pcPosition, tradeResult);
		}else{
			this.closePostion(pcPosition, tradeResult);
		}
		
		//修改维持保证金率
		BigDecimal holdRatio = this.feeRatioService.getHoldRatio(pcPosition.getUserId(), pcPosition.getAsset(), pcPosition.getSymbol(), pcPosition.getVolume());
		pcPosition.setHoldMarginRatio(holdRatio);
		
		//如果升档
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
		
		return pcPosition;
    }
	
	private int getLogType(int closeFlag, int longFlag){
		if(IntBool.isFalse(closeFlag)){
			return IntBool.isTrue(longFlag)?LogType.TYPE_TRAD_OPEN_LONG:LogType.TYPE_TRAD_OPEN_SHORT;
		}else{
			return IntBool.isTrue(longFlag)?LogType.TYPE_TRAD_CLOSE_LONG:LogType.TYPE_TRAD_CLOSE_SHORT;
		}
	}
	
	private void closeMarginToPcAccount(Long userId, Long orderTradeId, String asset, BigDecimal amount, int longFlag, String remark) {
		PcAddRequest request = new PcAddRequest();
		if(BigUtils.isZero(amount)){
			logger.warn("没有保证金可以返还");
			return;
		}
		if(BigUtils.ltZero(amount)){
			logger.error("穿仓。。。。");
			return;
		}
		request.setAmount(amount);
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark(remark);
		request.setTradeNo("CLOSE-"+orderTradeId);
		request.setTradeType(IntBool.isTrue(longFlag)?PcAccountTradeType.ORDER_CLOSE_LONG:PcAccountTradeType.ORDER_CLOSE_SHORT);
		request.setAssociatedId(orderTradeId);
		this.accountCoreService.add(request);
	}
	
	private void returnRemainOpenfee(Long userId, Long orderTradeId, String asset, BigDecimal orderOpenFee) {
		PcAddRequest request = new PcAddRequest();
		request.setAmount(orderOpenFee.add(orderOpenFee));
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark(String.format("返还剩余开仓手续费：%s", orderOpenFee, orderOpenFee));
		request.setTradeNo("CLOSE-"+orderTradeId);
		request.setTradeType(PcAccountTradeType.RETURN_FEE_DIFF);
		request.setAssociatedId(orderTradeId);
		this.accountCoreService.add(request);
	}

	private void updateOrder4Trade(PcOrder order, TradeResult tradeResult, Long now){
		if(order.getCloseFlag() == OrderFlag.ACTION_OPEN){
	        order.setOrderMargin(order.getOrderMargin().subtract(tradeResult.getOrderMargin()));
	        order.setOpenFee(order.getOpenFee().subtract(tradeResult.getFee()));
	        order.setCloseFee(order.getCloseFee().subtract(tradeResult.getOrderCloseFee()));
		}
		order.setFeeCost(order.getFeeCost().add(tradeResult.getFee()));
		order.setFilledVolume(order.getFilledVolume().add(tradeResult.getNumber()));
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

		orderTrade.setOrderMargin(tradeResult.getOrderMargin());
		orderTrade.setFee(tradeResult.getFee());
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
		
		orderTrade.setRemainVolume(order.getVolume().subtract(order.getFilledVolume()));
		
		orderTrade.setMatchTxId(tradeMsg.getMatchTxId());
		
		orderTrade.setOpponentOrderId(tradeMsg.getOpponentOrderId());
		
		orderTrade.setFeeSynchStatus(IntBool.NO);
		
		this.orderTradeDAO.save(orderTrade);
		
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
		
		pcPosition.setVersion(0L);
		
		return pcPosition;
	}

	private void openPositon(PcPosition pcPosition, TradeResult tradeResult) {
		pcPosition.setVolume(pcPosition.getVolume().add(tradeResult.getNumber()));
		pcPosition.setPosMargin(pcPosition.getPosMargin().add(tradeResult.getOrderMargin()));
		pcPosition.setCloseFee(pcPosition.getCloseFee().add(tradeResult.getOrderCloseFee()));
		
		pcPosition.setMeanPrice(tradeResult.getNewPosMeanPrice());
		pcPosition.setInitMargin(pcPosition.getInitMargin().add(tradeResult.getOrderMargin()));
//		pcPosition.setFeeCost(pcPosition.getFeeCost().add(tradeResult.getReceivableFee()));
		
		pcPosition.setAccuVolume(pcPosition.getAccuVolume().add(tradeResult.getNumber()));
		
		pcPosition.setAccuBaseValue(pcPosition.getAccuBaseValue().add(tradeResult.getBaseValue()));

		pcPosition.setBaseValue(positionStrategy.calcBaseValue(pcPosition.getAsset(), pcPosition.getSymbol(), pcPosition.getVolume(), pcPosition.getFaceValue(), pcPosition.getMeanPrice()));
		pcPosition.setLiqPrice(tradeResult.getNewPosLiqPrice());
		
	}

	private void closePostion(PcPosition pcPosition, TradeResult tradeResult) {
		pcPosition.setVolume(pcPosition.getVolume().subtract(tradeResult.getNumber()));
		pcPosition.setPosMargin(pcPosition.getPosMargin().subtract(tradeResult.getOrderMargin()));
		pcPosition.setCloseFee(pcPosition.getCloseFee().subtract(tradeResult.getFee()));
		
		pcPosition.setInitMargin(pcPosition.getInitMargin().subtract(tradeResult.getOrderMargin()));
		pcPosition.setFeeCost(pcPosition.getFeeCost().subtract(tradeResult.getFee()));
		
		pcPosition.setRealisedPnl(pcPosition.getRealisedPnl().add(tradeResult.getPnl()));

		pcPosition.setLiqPrice(tradeResult.getNewPosLiqPrice());
		pcPosition.setBaseValue(positionStrategy.calcBaseValue(pcPosition.getAsset(), pcPosition.getSymbol(), pcPosition.getVolume(), pcPosition.getFaceValue(), pcPosition.getMeanPrice()));
		pcPosition.setMeanPrice(tradeResult.getNewPosMeanPrice());

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
		Long count = this.orderTradeDAO.exist(order.getUserId(), tradeMsg.uniqueKey());
		if(count>0){
			return false;
		}
		return true;
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
	

	@Autowired
	private PcCollectorCoreService collectorCoreService;

	public void synchCollector(PcOrderTrade orderTrade){
		if(BigUtils.gtZero(orderTrade.getFee())){
			CollectorAddRequest request = new CollectorAddRequest();
			request.setAmount(orderTrade.getFee());
			request.setAsset(orderTrade.getAsset());
			request.setAssociatedId(orderTrade.getOrderId());
			request.setRemark("手续费");
			request.setTradeNo(""+orderTrade.getId());
			request.setTradeType(0);
			request.setUserId(orderTrade.getUserId());
			request.setCollectorId(orderTrade.getFeeCollectorId());
			collectorCoreService.add(request);
		}else{
			CollectorCutRequest request = new CollectorCutRequest();
			request.setAmount(orderTrade.getFee());
			request.setAsset(orderTrade.getAsset());
			request.setAssociatedId(orderTrade.getOrderId());
			request.setRemark("倒贴手续费");
			request.setTradeNo(""+orderTrade.getId());
			request.setTradeType(0);
			request.setUserId(orderTrade.getUserId());
			request.setCollectorId(orderTrade.getFeeCollectorId());
			collectorCoreService.cut(request);
		}
	}

	public void setSynchStatus(PcOrderTrade orderTrade){
		Long now = DbDateUtils.now();
		this.orderTradeDAO.setSynchStatus(orderTrade.getUserId(), orderTrade.getId(), IntBool.YES, now);
	}
	
}
