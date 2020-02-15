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
import com.hp.sh.expv3.bb.strategy.common.CommonOrderStrategy;
import com.hp.sh.expv3.bb.strategy.vo.OrderRatioData;
import com.hp.sh.expv3.bb.vo.request.BBAddRequest;
import com.hp.sh.expv3.bb.vo.request.BBCutRequest;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.constant.InvokeResult;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.SnUtils;
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
	private CommonOrderStrategy orderStrategy;	
	
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
			String remark = "买"+bs.getPayCurrency()+",押金="+order.getOrderMargin()+"，手续费="+order.getFee().stripTrailingZeros();
			this.cutMargin(userId, bs.getPayCurrency(), order.getId(), order.getGrossMargin(), remark);
		}else{
			String remark = "买"+bs.getPayCurrency()+",押金="+order.getVolume()+"，手续费无";
			this.cutMargin(userId, bs.getPayCurrency(), order.getId(), order.getVolume(), remark);
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
	
	private void cutMargin(Long userId, String asset, Long orderId, BigDecimal amount, String remark){
		BBCutRequest request = new BBCutRequest();
		request.setAmount(amount);
		request.setAsset(asset);
		request.setRemark("币币委托:"+remark);
		request.setTradeNo(SnUtils.getOrderPaySn(""+orderId));
		request.setTradeType(BBAccountTradeType.ORDER);
		request.setUserId(userId);
		request.setAssociatedId(orderId);
		this.bBAccountCoreService.cut(request);
	}
	
	private Integer returnCancelAmt(Long userId, String asset, Long orderId, BigDecimal orderMargin, BigDecimal fee){
		BBAddRequest request = new BBAddRequest();
		request.setAmount(orderMargin.add(fee));
		request.setAsset(asset);
		request.setRemark("撤单还余额:押金="+orderMargin+",手续费="+fee);
		request.setTradeNo(SnUtils.getCancelOrderReturnSn(""+orderId));
		SnUtils.getSynchReturnSn(""+orderId);
		request.setTradeType(BBAccountTradeType.ORDER_CANCEL);
		request.setUserId(userId);
		request.setAssociatedId(orderId);
		return this.bBAccountCoreService.add(request);
	}

	//设置开仓订单的各种费率
	private void setFee(BBOrder order) {
		BigDecimal feeRatio = this.feeRatioService.getTakerFeeRatio(order.getUserId(), order.getAsset(), order.getSymbol());
		order.setFeeRatio(feeRatio);
		
		OrderRatioData ratioData = orderStrategy.calcOrderAmt(order);
		//押金
		if(order.getBidFlag()==OrderFlag.BID_BUY){
			order.setOrderMargin(ratioData.getOrderMargin());
		}else{
			order.setOrderMargin(order.getVolume());
		}
		//手续费
		order.setFee(ratioData.getFee());

		BBSymbol bs = new BBSymbol(order.getSymbol(), order.getBidFlag());
		order.setOrderMarginCurrency(bs.getPayCurrency());
	}
	
	@LockIt(key="${userId}-${asset}-${symbol}")
	public void setPendingCancel(long userId, String asset, String symbol, long orderId){
		
		BBOrder order = this.orderQueryService.getOrder(userId, orderId);
		
		if(!this.canCancel(order, orderId)){
			logger.info("订单无法取消：{}", order);
			return;
		}
		
		Long now = DbDateUtils.now();
		orderUpdateService.setUserCancelStatus(orderId, userId, OrderStatus.PENDING_CANCEL, now, OrderStatus.CANCELED, OrderStatus.FILLED, IntBool.YES);
	}
	
	private boolean canCancel(BBOrder order, Long orderId){
		if(order==null){
			logger.error("订单不存在：orderId={}", orderId);
			return false;
		}
		if(order.getStatus() == OrderStatus.CANCELED){
			return false;
		}
		if(order.getStatus() == OrderStatus.FAILED){
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
		if(order.getBidFlag()==OrderFlag.BID_BUY){
//			
			OrderRatioData ratioData = orderStrategy.calcRaitoAmt(order, remaining);
			
			int result = this.returnCancelAmt(userId, asset, orderId, ratioData.getOrderMargin(), ratioData.getFee());
			if(result==InvokeResult.NOCHANGE){
				//利用合约账户的幂等性实现本方法的幂等性
				logger.warn("已经执行过了");
				return;
			}
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
		long now = DbDateUtils.now();
		this.orderUpdateService.setNewStatus(orderId, userId, OrderStatus.NEW, OrderStatus.PENDING_NEW, now);
	}
	
}
