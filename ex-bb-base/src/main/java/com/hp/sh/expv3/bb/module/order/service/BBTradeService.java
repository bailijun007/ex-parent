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
import com.hp.sh.expv3.bb.strategy.common.CommonOrderStrategy;
import com.hp.sh.expv3.bb.strategy.vo.BBTradeVo;
import com.hp.sh.expv3.bb.strategy.vo.TradeResult;
import com.hp.sh.expv3.bb.vo.request.BBAddRequest;
import com.hp.sh.expv3.commons.exception.ExSysException;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.SnUtils;
import com.hp.sh.expv3.utils.math.BigCalc;
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
	public void handleTrade(BBTradeVo trade){
		BBOrder order = this.orderQueryService.getOrder(trade.getAccountId(), trade.getOrderId());
		boolean yes = this.canTrade(order, trade);
		if(!yes){
			logger.error("成交已处理过了");
			return;
		}
		
		Long now = DbDateUtils.now();
		
		//计算
		TradeResult tradeResult = orderStrategy.calcTradeResult(trade, order);
		
		////////// 成交记录  ///////////
		BBOrderTrade orderTrade = this.saveOrderTrade(trade, order, tradeResult, now);
		
		// 转账
		BBSymbol bs = new BBSymbol(orderTrade.getSymbol(), orderTrade.getBidFlag());
		if(order.getBidFlag()==OrderFlag.BID_BUY){
			BigDecimal income = orderTrade.getVolume();
			String remark = String.format("购买成交：%s", income);
			this.addBalance(orderTrade.getUserId(), orderTrade.getId(), bs.getIncomeCurrency(), income, remark);
		}else{
			BigDecimal amount = orderTrade.getVolume().multiply(orderTrade.getPrice());
			BigDecimal income = amount.subtract(orderTrade.getFee());
			String remark = String.format("销售成交：%s", amount);
			this.addBalance(orderTrade.getUserId(), orderTrade.getId(), bs.getIncomeCurrency(), income, remark);
		}
		
		//修改订单状态
		this.updateOrder4Trade(order, orderTrade, now);
		
		//退还剩余押金和手续费
		if(orderTrade.isOrderCompleted()){
			if(order.getBidFlag()==OrderFlag.BID_BUY){
				if(BigUtils.gtZero(orderTrade.getRemainFee()) || BigUtils.gtZero(orderTrade.getRemainOrderMargin())){
					this.returnRemaining(order.getUserId(), order.getAsset(), order.getId(), orderTrade.getRemainFee(), orderTrade.getRemainOrderMargin());
				}
			}
		}
		
		//同步手续费
		
		//补充手续费
		
		
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
	
	private void addBalance(Long userId, Long orderTradeId, String asset, BigDecimal amount, String remark) {
		BBAddRequest request = new BBAddRequest();
		request.setAmount(amount);
		request.setUserId(userId);
		request.setAsset(asset);
		request.setRemark(remark);
		request.setTradeNo(SnUtils.getIncomeSn(orderTradeId));
		request.setTradeType(BBAccountTradeType.INCOME);
		request.setAssociatedId(orderTradeId);
		this.pcAccountCoreService.add(request);
	}

	private void updateOrder4Trade(BBOrder order, BBOrderTrade tradeResult, Long now){
        if(tradeResult.isOrderCompleted()){
        	order.setOrderMargin(BigDecimal.ZERO);
        	order.setFee(BigDecimal.ZERO);
        }else{
        	//扣除押金
            order.setOrderMargin(tradeResult.getRemainOrderMargin());
        	//扣除手续费
        	order.setFee(tradeResult.getRemainFee());
        }
        
        //增加已扣手续费
		order.setFeeCost(order.getFeeCost().add(tradeResult.getFee()));
		//增加已成交金额
		order.setFilledVolume(order.getFilledVolume().add(tradeResult.getVolume()));
		//全部成交/部分成交
        order.setStatus(tradeResult.isOrderCompleted()?OrderStatus.FILLED:OrderStatus.PARTIALLY_FILLED);
        //活动状态
        order.setActiveFlag(tradeResult.isOrderCompleted()?BBOrder.NO:BBOrder.YES);
        //修改时间
		order.setModified(now);
		this.orderUpdateService.updateOrder4Trad(order);
	}

	private BBOrderTrade saveOrderTrade(BBTradeVo tradeMsg, BBOrder order, TradeResult tradeResult, Long now) {
		BBOrderTrade orderTrade = new BBOrderTrade();

		orderTrade.setId(null);
		orderTrade.setAsset(order.getAsset());
		orderTrade.setSymbol(order.getSymbol());
		orderTrade.setVolume(tradeResult.getTradeVolume());
		orderTrade.setPrice(tradeResult.getTradePrice());
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
		
		orderTrade.setMatchTxId(tradeMsg.getMatchTxId());
		
		orderTrade.setRemainVolume(BigCalc.subtract(order.getVolume(), order.getFilledVolume(), tradeResult.getTradeVolume()));
		orderTrade.setRemainOrderMargin(BigCalc.subtract(order.getOrderMargin(), tradeResult.getTradeOrderMargin()));
		orderTrade.setRemainFee(BigCalc.subtract(order.getFee(), tradeResult.getReceivableFeeRatio()));
		
		orderTrade.setFeeCollectorId(feeCollectorSelector.getFeeCollectorId(order.getUserId(), order.getAsset(), order.getSymbol()));
		orderTrade.setFeeSynchStatus(IntBool.NO);
		
		this.pcOrderTradeDAO.save(orderTrade);
		
		orderTrade.setLogType(this.getLogType(order.getBidFlag()));
		
		publisher.publishEvent(orderTrade);
		
		return orderTrade;
	}

	//检查订单状态
	private boolean canTrade(BBOrder order, BBTradeVo tradeMsg) {
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
	
	public void synchCollector(Long userId, Long tradeOrderId, Long feeCollectorId, BigDecimal fee){
		
	}

}
