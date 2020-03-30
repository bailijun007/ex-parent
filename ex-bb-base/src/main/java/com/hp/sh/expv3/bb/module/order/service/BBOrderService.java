package com.hp.sh.expv3.bb.module.order.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.bb.component.FeeRatioService;
import com.hp.sh.expv3.bb.constant.BBAccountTradeType;
import com.hp.sh.expv3.bb.constant.BBOrderType;
import com.hp.sh.expv3.bb.constant.OrderFlag;
import com.hp.sh.expv3.bb.constant.OrderStatus;
import com.hp.sh.expv3.bb.module.account.service.BBAccountCoreService;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.vo.BBSymbol;
import com.hp.sh.expv3.bb.strategy.common.BBCommonOrderStrategy;
import com.hp.sh.expv3.bb.strategy.vo.OrderFeeData;
import com.hp.sh.expv3.bb.vo.request.FreezeRequest;
import com.hp.sh.expv3.bb.vo.request.UnFreezeRequest;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.constant.InvokeResult;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.SnUtils;
import com.hp.sh.expv3.utils.math.BigFormat;
import com.hp.sh.expv3.utils.math.BigUtils;

/**
 * 委托
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class BBOrderService {
	private static final Logger logger = LoggerFactory.getLogger(BBOrderService.class);
	
	@Autowired
	private BBOrderUpdateService orderUpdateService;
	
	@Autowired
	private BBOrderQueryService orderQueryService;
	
	@Autowired
	private FeeRatioService feeRatioService;
	
	@Autowired
	private BBAccountCoreService bBAccountCoreService;
	
	@Autowired
	private BBCommonOrderStrategy orderStrategy;
	
	/**
	 * 创建订单
	 * @param userId 用户ID
	 * @param cliOrderId 客户端订单ID
	 * @param asset 资产
	 * @param symbol 合约品种
	 * @param bidFlag 买卖
	 * @param timeInForce 生效机制
	 * @param price 委托价格
	 * @param amt 委托金额
	 */
	@LockIt(key="${userId}-${asset}-${symbol}")
	public BBOrder create(long userId, String cliOrderId, String asset, String symbol, int bidFlag, int timeInForce, BigDecimal price, BigDecimal number){
		
//		if(this.existClientOrderId(userId, cliOrderId)){
//			throw new ExException(BBOrderError.CREATED);
//		}
		
		Long now = DbDateUtils.now();
		
		//订单基本数据
		BBOrder order = new BBOrder();
		
		order.setAsset(asset);
		order.setSymbol(symbol);
		order.setBidFlag(bidFlag);
		order.setVolume(number);
		order.setPrice(price);
		
		order.setOrderType(BBOrderType.LIMIT);
		order.setTimeInForce(timeInForce);
		order.setUserId(userId);
		order.setCreated(now);
		order.setModified(now);
		order.setStatus(OrderStatus.PENDING_NEW);
		order.setClientOrderId(cliOrderId);
		order.setActiveFlag(IntBool.YES);
		order.setVersion(0L);
		
		this.setFee(order);
		
		////////其他字段，后面随状态修改////////
		this.setOther(order);
		
		this.orderUpdateService.saveOrder(order);
		
		//押金
		BBSymbol bs = new BBSymbol(symbol, bidFlag);
		if(order.getBidFlag()==OrderFlag.BID_BUY){
			String remark = BigFormat.format("买,押金=%s%s，手续费=%s%s", order.getOrderMargin(), bs.getMarginCurrency(), order.getFee(), asset);
			this.cutMargin(userId, bs.getMarginCurrency(), order.getId(), order.getGrossMargin(), BBAccountTradeType.ORDER_BUY, remark);
		}else{
			String remark = BigFormat.format("卖,押金=%s%s，手续费=%s%s", order.getVolume(), bs.getMarginCurrency(), "无", "");
			this.cutMargin(userId, bs.getMarginCurrency(), order.getId(), order.getVolume(), BBAccountTradeType.ORDER_SELL, remark);
		}
		
		return order;
	}

	private boolean existClientOrderId(long userId, String clientOrderId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("clientOrderId", clientOrderId);
		Long count = this.orderQueryService.queryCount(params);
		return count>0;
	}

	private void setOther(BBOrder bBOrder){
		bBOrder.setFeeCost(BigDecimal.ZERO);
		bBOrder.setFilledVolume(BigDecimal.ZERO);
		bBOrder.setCancelVolume(null);
		bBOrder.setCancelTime(null);

		////////////log////////////
		bBOrder.setCreateOperator(null);
		bBOrder.setCancelOperator(null);
		bBOrder.setRemark(null);
		
		///////////其他///
		bBOrder.setCancelVolume(BigDecimal.ZERO);
	}
	
	private void cutMargin(Long userId, String asset, Long orderId, BigDecimal amount , int tradeType, String remark){
		FreezeRequest request = new FreezeRequest();
		request.setAmount(amount);
		request.setAsset(asset);
		request.setRemark("币币委托:"+remark);
		request.setTradeNo(SnUtils.getOrderPaySn(""+orderId));
		request.setTradeType(tradeType);
		request.setUserId(userId);
		request.setAssociatedId(orderId);
		this.bBAccountCoreService.freeze(request);
	}
	
	private Integer returnCancelAmt(Long userId, String asset, Long orderId, BigDecimal orderMargin, BigDecimal fee, BigDecimal cancelVolume){
		UnFreezeRequest request = new UnFreezeRequest();
		request.setAmount(orderMargin.add(fee));
		request.setAsset(asset);
		String remark = BigFormat.format("撤单:押金=%s,手续费=%s,撤销数量=%s", orderMargin, fee, cancelVolume);
		request.setRemark(remark);
		request.setTradeNo(SnUtils.getCancelOrderReturnSn(""+orderId));
		request.setTradeType(BBAccountTradeType.ORDER_CANCEL);
		request.setUserId(userId);
		request.setAssociatedId(orderId);
		return this.bBAccountCoreService.unfreeze(request);
	}

	//设置开仓订单的各种费率
	private void setFee(BBOrder order) {
		BigDecimal feeRatio = this.feeRatioService.getTakerFeeRatio(order.getUserId(), order.getAsset(), order.getSymbol());
		order.setFeeRatio(feeRatio);
		
		OrderFeeData orderFeeData = orderStrategy.calcOrderAmt(order);
		//押金 & 手续费
		if(order.getBidFlag()==OrderFlag.BID_BUY){
			order.setOrderMargin(orderFeeData.getOrderMargin());
			order.setFee(orderFeeData.getFee());
		}else{
			order.setOrderMargin(order.getVolume());
			order.setFee(BigDecimal.ZERO);
		}

		BBSymbol bs = new BBSymbol(order.getSymbol(), order.getBidFlag());
		order.setOrderMarginCurrency(bs.getMarginCurrency());
	}
	
	@LockIt(key="${userId}-${asset}-${symbol}")
	public boolean setPendingCancel(long userId, String asset, String symbol, long orderId){
		
		BBOrder order = this.orderQueryService.getOrder(userId, orderId);
		
		if(!this.canCancel(order, orderId)){
			logger.info("订单无法取消：{}", order);
			return false;
		}
		
		Long now = DbDateUtils.now();
		orderUpdateService.setPendingCancel(OrderStatus.PENDING_CANCEL, now, orderId, userId, order.getVersion());
		return true;
	}
	
	private boolean canCancel(BBOrder order, Long orderId){
		if(order==null){
			logger.error("订单不存在：orderId={}", orderId);
			return false;
		}
		if(order.getStatus() == OrderStatus.CANCELED){
			return false;
		}
		if(order.getStatus() == OrderStatus.FILLED){
			return false;
		}
		if(BigUtils.eq(order.getVolume(), order.getFilledVolume())){
			return false;
		}
		if(IntBool.isFalse(order.getActiveFlag())){
			return false;
		}
		return true;
	}

	@LockIt(key="${userId}-${asset}-${symbol}")
	public void setCancelled(long userId, String asset, String symbol, long orderId){
		this.doCancel(userId, asset, symbol, orderId);
	}

	/**
	 * 撤销委托
	 * @param userId
	 * @param asset
	 * @param orderId 订单ID
	 * @param number 撤几张合约
	 */
	private void doCancel(long userId, String asset, String symbol, long orderId){
		BBOrder order = this.orderQueryService.getOrder(userId, orderId);
		if(order==null){
			logger.error("订单不存在:orderId={}", orderId);
			return;
		}
		if(order.getStatus() == OrderStatus.CANCELED){
			logger.warn("订单已经是取消状态了");
			return;
		}
		
		if(!this.canCancel(order, orderId)){
			logger.info("订单无法取消：{}", order);
			return;
		}

		BigDecimal remaining = order.getVolume().subtract(order.getFilledVolume());
		
		//退押金
		BBSymbol bs = new BBSymbol(symbol, order.getBidFlag());
		int result = this.returnCancelAmt(userId, bs.getMarginCurrency(), orderId, order.getOrderMargin(), order.getFee(), remaining);
		if(result==InvokeResult.NOCHANGE){
			//利用合约账户的幂等性实现本方法的幂等性
			logger.warn("已经执行过了");
			return;
		}
		
		//修改订单状态（撤销）
		Long now = DbDateUtils.now();
		order.setCancelTime(now);
		order.setCancelVolume(remaining);
		order.setActiveFlag(BBOrder.NO);
		order.setStatus(OrderStatus.CANCELED);
		
		this.orderUpdateService.updateOrder(order, now);
		
		return;
	}

	@LockIt(key="${userId}-${asset}-${symbol}")
	public void setNewStatus(long userId, String asset, String symbol, long orderId){
		BBOrder order = this.orderQueryService.getOrder(userId, orderId);
		
		if(order.getStatus()!=OrderStatus.PENDING_NEW){
			logger.error("NEW状态错误，orderId={}", orderId, order.getStatus());
			return ;
		}

		long now = DbDateUtils.now();
		
		this.orderUpdateService.setNewStatus(order, now);
	}
	
}
