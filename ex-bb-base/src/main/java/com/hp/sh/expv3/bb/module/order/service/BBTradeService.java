package com.hp.sh.expv3.bb.module.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.base.exceptions.CommonError;
import com.hp.sh.expv3.bb.component.FeeCollectorSelector;
import com.hp.sh.expv3.bb.constant.BBAccountTradeType;
import com.hp.sh.expv3.bb.constant.OrderFlag;
import com.hp.sh.expv3.bb.constant.OrderStatus;
import com.hp.sh.expv3.bb.error.BBOrderError;
import com.hp.sh.expv3.bb.module.account.service.BBAccountCoreService;
import com.hp.sh.expv3.bb.module.collector.service.BBCollectorCoreService;
import com.hp.sh.expv3.bb.module.order.dao.BBOrderTradeDAO;
import com.hp.sh.expv3.bb.module.order.dao.BBOrderTradeSnDAO;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderTrade;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderTradeSn;
import com.hp.sh.expv3.bb.module.order.vo.BBSymbol;
import com.hp.sh.expv3.bb.mq.msg.in.BBTradeMsg;
import com.hp.sh.expv3.bb.strategy.common.BBCommonOrderStrategy;
import com.hp.sh.expv3.bb.strategy.data.OrderTrade;
import com.hp.sh.expv3.bb.strategy.vo.OrderTradeVo;
import com.hp.sh.expv3.bb.strategy.vo.TradeResult;
import com.hp.sh.expv3.bb.vo.request.BBAddRequest;
import com.hp.sh.expv3.bb.vo.request.CollectorAddRequest;
import com.hp.sh.expv3.bb.vo.request.CollectorCutRequest;
import com.hp.sh.expv3.bb.vo.request.ReleaseFrozenRequest;
import com.hp.sh.expv3.bb.vo.request.UnFreezeRequest;
import com.hp.sh.expv3.commons.exception.ExSysException;
import com.hp.sh.expv3.component.dbshard.impl.TradeId2DateShard;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.SnUtils;
import com.hp.sh.expv3.utils.math.BigCalc;
import com.hp.sh.expv3.utils.math.BigFormat;
import com.hp.sh.expv3.utils.math.BigUtils;

@Service
@Transactional(rollbackFor=Exception.class)
public class BBTradeService {
	private static final Logger logger = LoggerFactory.getLogger(BBTradeService.class);

	@Autowired
	private BBOrderTradeDAO orderTradeDAO;
	
	@Autowired
	private TradeId2DateShard tradeId;
	
	@Autowired
	private BBOrderUpdateService orderUpdateService;
	
	@Autowired
	private BBOrderQueryService orderQueryService;
	
	@Autowired
	private BBAccountCoreService accountCoreService;
	
	@Autowired
	private BBCommonOrderStrategy orderStrategy;
	
	@Autowired
	private FeeCollectorSelector feeCollectorSelector;
	
	@Autowired
	private BBOrderTradeSnDAO orderTradeSnDAO;
	
    @Autowired
    private ApplicationEventPublisher publisher;
	
	//处理成交订单
	public void handleTrade(BBTradeMsg msg){
		BBOrder order = this.orderQueryService.getOrder(msg.getAsset(), msg.getSymbol(), msg.getAccountId(), msg.getOrderId());
		boolean yes = this.canTrade(order, msg);
		if(!yes){
			logger.warn("成交已处理过了,trade={}", msg);
			return;
		}
		
		Long now = DbDateUtils.now();
		
		//计算
		TradeResult tradeResult = orderStrategy.calcTradeResult(msg, order);
		
		////////// 成交记录  ///////////
		BBOrderTrade orderTrade = this.saveOrderTrade(msg, order, tradeResult, now);
		
		// 转账
		BBSymbol bs = new BBSymbol(orderTrade.getSymbol(), orderTrade.getBidFlag());
		if(order.getBidFlag()==OrderFlag.BID_BUY){
			BigDecimal income = orderTrade.getVolume();
			String remark = BigFormat.format("购买成交获得：%s", income);
			this.addBuyIn(orderTrade.getUserId(), orderTrade.getId(), orderTrade.getOrderId(), bs.getIncomeCurrency(), income, remark);
		}else{
			BigDecimal amount = orderTrade.getVolume().multiply(orderTrade.getPrice());
			BigDecimal income = amount.subtract(orderTrade.getFee());
			String remark = BigFormat.format("销售成交收入：%s，手续费-%s", amount, orderTrade.getFee());
			this.addSellIncome(orderTrade.getUserId(), orderTrade.getId(), orderTrade.getOrderId(), bs.getIncomeCurrency(), income, remark);
		}
		
		//修改订单状态
		this.updateOrder4Trade(order, orderTrade, now);
		
		//释放冻结的保证金，手续费
		if(order.getBidFlag()==OrderFlag.BID_BUY){
			this.releaseCurrencyMargin(order.getUserId(), order.getOrderMarginCurrency(), orderTrade.getId(), order.getId(), orderTrade.getFee(), tradeResult.getTradeOrderMargin());
		}else{
			this.releaseAssetMargin(order.getUserId(), order.getOrderMarginCurrency(), orderTrade.getId(), order.getId(), BigDecimal.ZERO, tradeResult.getTradeOrderMargin());
		}
		
		//退还剩余押金和手续费
		if(orderTrade.isOrderCompleted()){
			if(BigUtils.gtZero(orderTrade.getRemainFee()) || BigUtils.gtZero(orderTrade.getRemainOrderMargin())){
				logger.info("剩余押金={}，手续费={}", orderTrade.getRemainOrderMargin(), orderTrade.getRemainFee());
				this.returnRemaining(order.getUserId(), order.getAsset(), order.getId(), orderTrade.getRemainFee(), orderTrade.getRemainOrderMargin());
			}
		}
		
	}

	private void returnRemaining(Long userId, String asset, Long orderId, BigDecimal remainFee, BigDecimal remainOrderMargin) {
		BigDecimal returnAmount = remainFee.add(remainOrderMargin);
		UnFreezeRequest request = new UnFreezeRequest();
		request.setAmount(returnAmount);
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark(BigFormat.format("剩余押金：%s，剩余手续费：%s", remainOrderMargin, remainFee));
		request.setTradeNo(SnUtils.getRemainSn(orderId));
		request.setTradeType(BBAccountTradeType.RETURN_ORDER_MARGIN);
		request.setAssociatedId(orderId);
		this.accountCoreService.unfreeze(request);
	}

	private void releaseCurrencyMargin(Long userId, String asset, Long orderTradeId, Long orderId, BigDecimal remainFee, BigDecimal remainOrderMargin) {
		BigDecimal returnAmount = remainFee.add(remainOrderMargin);
		ReleaseFrozenRequest request = new ReleaseFrozenRequest();
		request.setAmount(returnAmount);
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark(BigFormat.format("释放冻结的押金：%s，手续费：%s", remainOrderMargin, remainFee));
		request.setTradeNo(SnUtils.getReleaseSn(orderTradeId));
		request.setTradeType(BBAccountTradeType.TRADE_BUY_RELEASE);
		request.setAssociatedId(orderId);
		this.accountCoreService.release(request);
	}

	private void releaseAssetMargin(Long userId, String asset, Long orderTradeId, Long orderId, BigDecimal remainFee, BigDecimal remainOrderMargin) {
		BigDecimal returnAmount = remainFee.add(remainOrderMargin);
		ReleaseFrozenRequest request = new ReleaseFrozenRequest();
		request.setAmount(returnAmount);
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark(BigFormat.format("释放冻结的押金：%s，手续费：%s", remainOrderMargin, remainFee));
		request.setTradeNo(SnUtils.getReleaseSn(orderTradeId));
		request.setTradeType(BBAccountTradeType.TRADE_SELL_RELEASE);
		request.setAssociatedId(orderId);
		this.accountCoreService.release(request);
	}

	private void addBuyIn(Long userId, Long orderTradeId, Long orderId, String asset, BigDecimal amount, String remark) {
		BBAddRequest request = new BBAddRequest();
		request.setAmount(amount);
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark(remark);
		request.setTradeNo(SnUtils.getBuyInSn(orderTradeId));
		request.setTradeType(BBAccountTradeType.TRADE_BUY_IN);
		request.setAssociatedId(orderId);
		this.accountCoreService.add(request);
	}

	private void addSellIncome(Long userId, Long orderTradeId, Long orderId, String asset, BigDecimal amount, String remark) {
		BBAddRequest request = new BBAddRequest();
		request.setAmount(amount);
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark(remark);
		request.setTradeNo(SnUtils.getSellIncomeSn(orderTradeId));
		request.setTradeType(BBAccountTradeType.TRADE_SELL_INCOME);
		request.setAssociatedId(orderId);
		this.accountCoreService.add(request);
	}

	private void updateOrder4Trade(BBOrder order, BBOrderTrade orderTrade, Long now){
        if(orderTrade.isOrderCompleted()){
        	order.setOrderMargin(BigDecimal.ZERO);
        	order.setFee(BigDecimal.ZERO);
        }else{
        	//扣除押金
            order.setOrderMargin(orderTrade.getRemainOrderMargin());
        	//扣除手续费
        	order.setFee(orderTrade.getRemainFee());
        }
        
		//成交均价
		List<OrderTrade> orderTradeList = new ArrayList<>();
		orderTradeList.add(orderTrade);
		orderTradeList.add(new OrderTradeVo(order.getFilledVolume(), order.getTradeMeanPrice()));
		BigDecimal tradeMeanPrice = orderStrategy.calcOrderMeanPrice(order.getAsset(), order.getSymbol(), orderTradeList);
		order.setTradeMeanPrice(tradeMeanPrice);
        
        //增加已扣手续费
		order.setFeeCost(order.getFeeCost().add(orderTrade.getFee()));
		//增加已成交金额
		order.setFilledVolume(order.getFilledVolume().add(orderTrade.getVolume()));
		//全部成交/部分成交
        order.setStatus(orderTrade.isOrderCompleted()?OrderStatus.FILLED:OrderStatus.PARTIALLY_FILLED);
        //活动状态
        order.setActiveFlag(orderTrade.isOrderCompleted()?BBOrder.NO:BBOrder.YES);
        //修改时间
		order.setModified(now);
		
		this.orderUpdateService.updateOrder4Trad(order);
		
	}

	private BBOrderTrade saveOrderTrade(BBTradeMsg tradeMsg, BBOrder order, TradeResult tradeResult, Long now) {
		BBOrderTrade orderTrade = new BBOrderTrade();

		orderTrade.setId(tradeId.genTradId(tradeMsg.getTradeTime()));
		orderTrade.setAsset(order.getAsset());
		orderTrade.setSymbol(order.getSymbol());
		orderTrade.setVolume(tradeResult.getTradeVolume());
		orderTrade.setPrice(tradeResult.getTradePrice());
		orderTrade.setMakerFlag(tradeMsg.getMakerFlag());
		orderTrade.setBidFlag(order.getBidFlag());

		orderTrade.setFee(tradeResult.getTradeFee());
		orderTrade.setFeeRatio(tradeResult.getTradeFeeRatio());
		
		orderTrade.setUserId(order.getUserId());
		orderTrade.setCreated(now);
		orderTrade.setModified(now);

		orderTrade.setTradeSn(tradeMsg.uniqueKey());
		orderTrade.setTradeId(tradeMsg.getTradeId());
		orderTrade.setTradeTime(tradeMsg.getTradeTime());
		
		orderTrade.setOrderId(order.getId());
		
		orderTrade.setMatchTxId(tradeMsg.getMatchTxId());
		
		orderTrade.setOpponentOrderId(tradeMsg.getOpponentOrderId());
		
		orderTrade.setOrderMargin(tradeResult.getTradeOrderMargin());
		
		orderTrade.setRemainVolume(BigCalc.subtract(order.getVolume(), order.getFilledVolume(), tradeResult.getTradeVolume()));
		orderTrade.setRemainOrderMargin(BigCalc.subtract(order.getOrderMargin(), tradeResult.getTradeOrderMargin()));
		
		if(IntBool.isTrue(order.getBidFlag())){
			if(BigUtils.gtZero(tradeResult.getTradeFee())){
				orderTrade.setRemainFee(BigCalc.subtract(order.getFee(), tradeResult.getTradeFee()));
			}else{//手续费是负数(倒贴),不扣订单手续费
				orderTrade.setRemainFee(order.getFee());
			}
		}else{
			orderTrade.setRemainFee(BigDecimal.ZERO);
		}
		
		orderTrade.setFeeCollectorId(feeCollectorSelector.getFeeCollectorId(order.getUserId(), order.getAsset(), order.getSymbol()));
		orderTrade.setFeeSynchStatus(IntBool.NO);
		
		//检查结果
		this.checkOrderTrade(orderTrade);
		
		this.orderTradeDAO.save(orderTrade);
		
		publisher.publishEvent(orderTrade);
		
		BBOrderTradeSn tradeSn = new BBOrderTradeSn(orderTrade.getTradeSn(), orderTrade.getId(), orderTrade.getTxId());
		orderTradeSnDAO.save(tradeSn);
		
		return orderTrade;
	}

	//检查订单状态
	private boolean canTrade1(BBOrder order, BBTradeMsg tradeMsg) {
		if(order==null){
			throw new ExSysException(CommonError.OBJ_DONT_EXIST, tradeMsg);
		}
		
		BigDecimal remainVol = order.getVolume().subtract(order.getFilledVolume());
		if(BigUtils.gt(tradeMsg.getNumber(), remainVol)){
			return false;
		}
		
		//检查重复请求
		Long count = this.orderTradeSnDAO.exist(tradeMsg.uniqueKey());
		if(count>0){
			return false;
		}
		return true;
	}
	
	//检查订单状态
	private boolean canTrade(BBOrder order, BBTradeMsg tradeMsg) {
		//检查重复请求
		Long count = this.orderTradeSnDAO.exist(tradeMsg.uniqueKey());
		if(count>0){
			return false;
		}
		
		if(order==null){
			logger.error("币币成交订单不存在：orderId={}", tradeMsg.getOrderId());
			throw new ExSysException(CommonError.OBJ_DONT_EXIST, tradeMsg);
//			return false;
		}
		
		////////////////
		if(order.getStatus() == OrderStatus.CANCELED){
			throw new ExSysException(BBOrderError.CANCELED, "canTrade", tradeMsg);
//			return false;
		}
		if(order.getStatus() == OrderStatus.FILLED){
			throw new ExSysException(BBOrderError.FILLED, "canTrade", tradeMsg);
		}
		if(BigUtils.eq(order.getVolume(), order.getFilledVolume())){
			throw new ExSysException(BBOrderError.NOT_ACTIVE, "canTrade", tradeMsg, "FilledVolume");
		}
		if(IntBool.isFalse(order.getActiveFlag())){
			throw new ExSysException(BBOrderError.NOT_ACTIVE, "canTrade", tradeMsg);
//			return false;
		}
		////////////////
		
		BigDecimal remainVol = order.getVolume().subtract(order.getFilledVolume());
		if(BigUtils.gt(tradeMsg.getNumber(), remainVol)){
			throw new ExSysException(BBOrderError.FILLED, "canTrade", tradeMsg, "gt");
		}

		return true;
	}
	
	private void checkOrderTrade(BBOrderTrade orderTrade) {
		if(BigUtils.ltZero(orderTrade.getRemainOrderMargin()) || BigUtils.ltZero(orderTrade.getRemainFee())){
			throw new RuntimeException("剩余保证金:"+orderTrade.getRemainOrderMargin()+"剩余手续费:"+orderTrade.getRemainFee());
		}
	}
	
	@Autowired
	private BBCollectorCoreService collectorCoreService;

	public void synchCollector(BBOrderTrade orderTrade){
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
		}else if(BigUtils.ltZero(orderTrade.getFee())){
			CollectorCutRequest request = new CollectorCutRequest();
			request.setAmount(orderTrade.getFee().abs());
			request.setAsset(orderTrade.getAsset());
			request.setAssociatedId(orderTrade.getOrderId());
			request.setRemark("倒贴手续费");
			request.setTradeNo(""+orderTrade.getId());
			request.setTradeType(0);
			request.setUserId(orderTrade.getUserId());
			request.setCollectorId(orderTrade.getFeeCollectorId());
			collectorCoreService.cut(request);
		}else{
			logger.warn("手续费为0, {}", orderTrade);
		}
		
	}
	
	public void setSynchStatus(BBOrderTrade orderTrade){
		Long now = DbDateUtils.now();
		this.orderTradeDAO.setSynchStatus(orderTrade.getAsset(), orderTrade.getSymbol(), orderTrade.getUserId(), orderTrade.getId(), IntBool.YES, now);
	}

}
