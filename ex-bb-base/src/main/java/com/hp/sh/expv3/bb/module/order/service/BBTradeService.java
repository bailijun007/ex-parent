package com.hp.sh.expv3.bb.module.order.service;

import java.math.BigDecimal;

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
import com.hp.sh.expv3.bb.constant.TradeRoles;
import com.hp.sh.expv3.bb.module.account.service.BBAccountCoreService;
import com.hp.sh.expv3.bb.module.order.dao.BBOrderTradeDAO;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderTrade;
import com.hp.sh.expv3.bb.module.order.vo.BBSymbol;
import com.hp.sh.expv3.bb.module.trade.entity.BBMatchedTrade;
import com.hp.sh.expv3.bb.msg.BBTradeMsg;
import com.hp.sh.expv3.bb.strategy.common.CommonOrderStrategy;
import com.hp.sh.expv3.bb.strategy.vo.TradeResult;
import com.hp.sh.expv3.bb.vo.request.BBAddRequest;
import com.hp.sh.expv3.commons.exception.ExSysException;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.SnUtils;
import com.hp.sh.expv3.utils.math.BigUtils;

@Service
@Transactional(rollbackFor=Exception.class)
public class BBTradeService {
	private static final Logger logger = LoggerFactory.getLogger(BBTradeService.class);

	@Autowired
	private BBOrderTradeDAO pcOrderTradeDAO;
	
	@Autowired
	private BBOrderUpdateService orderUpdateService;
	
	@Autowired
	private BBOrderQueryService orderQueryService;
	
	@Autowired
	private BBAccountCoreService pcAccountCoreService;
	
	@Autowired
	private CommonOrderStrategy orderStrategy;
	
	@Autowired
	private FeeCollectorSelector feeCollectorSelector;
	
    @Autowired
    private ApplicationEventPublisher publisher;
	
	//处理成交订单
    @LockIt(key="${trade.accountId}-${trade.asset}-${trade.symbol}")
	public void handleTrade(BBTradeMsg trade){
		BBOrder order = this.orderQueryService.getOrder(trade.getAccountId(), trade.getOrderId());
		boolean yes = this.canTrade(order, trade);
		if(!yes){
			logger.error("成交已处理过了");
			return;
		}
		
		Long now = DbDateUtils.now();
		
		TradeResult tradeResult = orderStrategy.calcTradeResult(trade, order);
		
		////////// 成交记录  ///////////
		BBOrderTrade pcOrderTrade = this.saveOrderTrade(trade, order, tradeResult, now);
		
		// 转账
		BBSymbol bs = new BBSymbol(pcOrderTrade.getSymbol(), pcOrderTrade.getBidFlag());
		if(order.getBidFlag()==OrderFlag.BID_BUY){
			BigDecimal income = pcOrderTrade.getVolume();
			this.addBalance(pcOrderTrade.getUserId(), pcOrderTrade.getId(), bs.getIncomeCurrency(), income);
		}else{
			BigDecimal amount = pcOrderTrade.getVolume().multiply(pcOrderTrade.getPrice());
			BigDecimal income = amount.subtract(pcOrderTrade.getFee());
			this.addBalance(pcOrderTrade.getUserId(), pcOrderTrade.getId(), bs.getIncomeCurrency(), income);
		}
		
		//修改订单状态
		this.updateOrder4Trade(order, tradeResult, now);
		
		//退还剩余押金和手续费
		if(tradeResult.getOrderCompleted()){
			if(order.getBidFlag()==OrderFlag.BID_BUY){
				if(BigUtils.gtZero(tradeResult.getRemainFee()) || BigUtils.gtZero(tradeResult.getRemainOrderMargin())){
					this.returnRemaining(order.getUserId(), order.getAsset(), order.getId(), tradeResult.getRemainFee(), tradeResult.getRemainOrderMargin());
				}
			}
		}
		
	}

	private void returnRemaining(Long userId, String asset, Long orderId, BigDecimal remainFee, BigDecimal remainOrderMargin) {
		BigDecimal returnAmount = remainFee.add(remainOrderMargin);
		BBAddRequest request = new BBAddRequest();
		request.setAmount(returnAmount);
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark(String.format("剩余押金：%s，剩余手续费：%s", remainOrderMargin, remainFee));
		request.setTradeNo(SnUtils.getRemainSn(orderId));
		request.setTradeType(BBAccountTradeType.INCOME);
		request.setAssociatedId(orderId);
		this.pcAccountCoreService.add(request);
	}

	private int getLogType(int bidFlag){
		return bidFlag;
	}
	
	private void addBalance(Long userId, Long orderTradeId, String asset, BigDecimal amount) {
		BBAddRequest request = new BBAddRequest();
		request.setAmount(amount);
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark(String.format("销售收入：%s", amount));
		request.setTradeNo(SnUtils.getIncomeSn(orderTradeId));
		request.setTradeType(BBAccountTradeType.INCOME);
		request.setAssociatedId(orderTradeId);
		this.pcAccountCoreService.add(request);
	}

	private void updateOrder4Trade(BBOrder order, TradeResult tradeResult, Long now){
        if(tradeResult.getOrderCompleted()){
        	order.setOrderMargin(BigDecimal.ZERO);
        	order.setFee(BigDecimal.ZERO);
        }else{
            order.setOrderMargin(tradeResult.getRemainOrderMargin());
        	order.setFee(tradeResult.getRemainFee());
        }
        
		order.setFeeCost(order.getFeeCost().add(tradeResult.getReceivableFee()));
		order.setFilledVolume(order.getFilledVolume().add(tradeResult.getVolume()));
        order.setStatus(tradeResult.getOrderCompleted()?OrderStatus.FILLED:OrderStatus.PARTIALLY_FILLED);
        order.setActiveFlag(tradeResult.getOrderCompleted()?BBOrder.NO:BBOrder.YES);
		order.setModified(now);
		this.orderUpdateService.updateOrder4Trad(order);
	}

	private BBOrderTrade saveOrderTrade(BBTradeMsg tradeMsg, BBOrder order, TradeResult tradeResult, Long now) {
		BBOrderTrade orderTrade = new BBOrderTrade();

		orderTrade.setId(null);
		orderTrade.setAsset(tradeMsg.getAsset());
		orderTrade.setSymbol(tradeMsg.getSymbol());
		orderTrade.setVolume(tradeMsg.getNumber());
		orderTrade.setPrice(tradeMsg.getPrice());
		orderTrade.setMakerFlag(tradeMsg.getMakerFlag());
		orderTrade.setBidFlag(order.getBidFlag());

		orderTrade.setFee(tradeResult.getReceivableFee());
		orderTrade.setFeeRatio(tradeResult.getReceivableFeeRatio());
		
		orderTrade.setUserId(order.getUserId());
		orderTrade.setCreated(now);
		orderTrade.setModified(now);

		orderTrade.setTradeSn(tradeMsg.uniqueKey());
		orderTrade.setTradeId(tradeMsg.getTradeId());
		orderTrade.setTradeTime(tradeMsg.getTradeTime());
		
		orderTrade.setOrderId(order.getId());
		
		orderTrade.setFeeCollectorId(feeCollectorSelector.getFeeCollectorId(order.getUserId(), order.getAsset(), order.getSymbol()));
		
		orderTrade.setRemainVolume(order.getVolume().subtract(order.getFilledVolume()).subtract(tradeResult.getVolume()));
		
		orderTrade.setMatchTxId(tradeMsg.getMatchTxId());
		
		this.pcOrderTradeDAO.save(orderTrade);
		
		orderTrade.setLogType(this.getLogType(order.getBidFlag()));
		
		publisher.publishEvent(orderTrade);
		
		return orderTrade;
	}

	//检查订单状态
	private boolean canTrade(BBOrder order, BBTradeMsg tradeMsg) {
		if(order==null){
			throw new ExSysException(CommonError.OBJ_DONT_EXIST);
		}
		
		BigDecimal remainVol = order.getVolume().subtract(order.getFilledVolume());
		if(BigUtils.gt(tradeMsg.getNumber(), remainVol)){
			return false;
		}
		
		//检查重复请求
		Long count = this.pcOrderTradeDAO.exist(order.getUserId(), tradeMsg.uniqueKey());
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
	public void handleMatchedResult(BBMatchedTrade pcMatchedResult){
		//taker
		BBTradeMsg takerTradeVo = new BBTradeMsg();
		takerTradeVo.setMakerFlag(TradeRoles.TAKER);
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
		BBTradeMsg makerTradeVo = new BBTradeMsg();
		makerTradeVo.setMakerFlag(TradeRoles.TAKER);
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
		this.handleTrade(takerTradeVo);
		
		//maker
		this.handleTrade(makerTradeVo);
		
	}
	
}
