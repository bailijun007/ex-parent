package com.hp.sh.expv3.bb.module.order.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.bb.calc.CompFieldCalc;
import com.hp.sh.expv3.bb.component.FeeRatioService;
import com.hp.sh.expv3.bb.component.MetadataService;
import com.hp.sh.expv3.bb.constant.LiqStatus;
import com.hp.sh.expv3.bb.constant.MarginMode;
import com.hp.sh.expv3.bb.constant.OrderFlag;
import com.hp.sh.expv3.bb.constant.OrderStatus;
import com.hp.sh.expv3.bb.constant.BBAccountTradeType;
import com.hp.sh.expv3.bb.constant.BBOrderType;
import com.hp.sh.expv3.bb.error.BBOrderError;
import com.hp.sh.expv3.bb.error.BBPositonError;
import com.hp.sh.expv3.bb.module.account.service.BBAccountCoreService;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.position.entity.BBPosition;
import com.hp.sh.expv3.bb.module.position.service.BBPositionDataService;
import com.hp.sh.expv3.bb.module.symbol.service.BBAccountSymbolService;
import com.hp.sh.expv3.bb.strategy.HoldPosStrategy;
import com.hp.sh.expv3.bb.strategy.common.CommonOrderStrategy;
import com.hp.sh.expv3.bb.strategy.vo.OrderRatioData;
import com.hp.sh.expv3.bb.vo.request.BBAddRequest;
import com.hp.sh.expv3.bb.vo.request.BBCutRequest;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.exception.ExSysException;
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
	private BBAccountSymbolService pcSymbolService;

	@Autowired
	private BBOrderUpdateService orderUpdateService;
	
	@Autowired
	private BBOrderQueryService orderQueryService;
	
	@Autowired
	private FeeRatioService feeRatioService;
	
	@Autowired
	private BBAccountCoreService bBAccountCoreService;
	
	@Autowired
	private MetadataService metadataService;
	
	@Autowired
	private CommonOrderStrategy orderStrategy;	
	
	@Autowired
	private BBPositionDataService positionDataService;
	
    @Autowired
    private HoldPosStrategy holdPosStrategy;
	
	@Autowired
	private BBOrderService self;
    
	/**
	 * 创建订单
	 * @param userId 用户ID
	 * @param cliOrderId 客户端订单ID
	 * @param asset 资产
	 * @param symbol 合约品种
	 * @param closeFlag 是否平仓：开/平
	 * @param longFlag 多/空
	 * @param timeInForce 生效机制
	 * @param price 委托价格
	 * @param amt 委托金额
	 */
	@LockIt(key="${userId}-${asset}-${symbol}")
	public BBOrder create(long userId, String cliOrderId, String asset, String symbol, int closeFlag, int longFlag, int timeInForce, BigDecimal price, BigDecimal number){
		BBPosition pos = this.positionDataService.getCurrentPosition(userId, asset, symbol, longFlag);
		return self.create(userId, cliOrderId, asset, symbol, closeFlag, longFlag, timeInForce, price, number, pos, IntBool.YES, IntBool.NO);
	}
	
	public BBOrder create(long userId, String cliOrderId, String asset, String symbol, int closeFlag, int longFlag, int timeInForce, BigDecimal price, BigDecimal number, BBPosition pos, Integer visibleFlag, int liqFlag){
		
//		if(this.existClientOrderId(userId, cliOrderId)){
//			throw new ExException(BBOrderError.CREATED);
//		}
		
		//非强平委托
		if(IntBool.isFalse(liqFlag)){
			if(closeFlag==OrderFlag.ACTION_CLOSE){
				this.checkLiqStatus(pos);
				// 检查可平仓位
				this.checkClosablePosition(pos, number);
				// 检查价格
				this.checkPrice(pos, price);
			}
		}
		
		Long now = DbDateUtils.now();
		
		
		//订单基本数据
		BBOrder bBOrder = new BBOrder();
		
		bBOrder.setAsset(asset);
		bBOrder.setSymbol(symbol);
		bBOrder.setCloseFlag(closeFlag);
		bBOrder.setLongFlag(longFlag);
		bBOrder.setLeverage(pcSymbolService.getLeverage(userId, asset, symbol, longFlag));
		bBOrder.setVolume(number);
		bBOrder.setFaceValue(metadataService.getFaceValue(asset, symbol));
		bBOrder.setPrice(price);
		
		bBOrder.setOrderType(BBOrderType.LIMIT);
		bBOrder.setMarginMode(MarginMode.FIXED);
		bBOrder.setTimeInForce(timeInForce);
		bBOrder.setUserId(userId);
		bBOrder.setCreated(now);
		bBOrder.setModified(now);
		bBOrder.setStatus(OrderStatus.PENDING_NEW);
		bBOrder.setClientOrderId(cliOrderId);
		bBOrder.setActiveFlag(IntBool.YES);
		bBOrder.setLiqFlag(liqFlag);
		
		/////////押金数据/////////
		
		if (IntBool.isTrue(closeFlag)) {
			this.setCloseOrderFee(bBOrder);
		}else{
			this.setOpenOrderFee(bBOrder);
		}
		
		////////其他字段，后面随状态修改////////
		this.setOther(bBOrder, pos);
		
		bBOrder.setVisibleFlag(visibleFlag);
		
		this.orderUpdateService.saveOrder(bBOrder);

		//开仓押金扣除
		if(closeFlag==OrderFlag.ACTION_OPEN){
			this.cutBalance(userId, asset, bBOrder.getId(), bBOrder.getGrossMargin(), longFlag);
		}
		
		return bBOrder;
	}
	
	private void checkLiqStatus(BBPosition pos) {
		if(pos==null){
			return;
		}
		if(pos.getLiqStatus() == LiqStatus.FROZEN){
			throw new ExSysException(BBPositonError.LIQING);
		}
		if(pos.getLiqStatus() == LiqStatus.FORCE_CLOSE){
			throw new ExException(BBPositonError.FORCE_CLOSE);
		}
	}

	private void checkPrice(BBPosition pos, BigDecimal price) {
		BigDecimal _amount = CompFieldCalc.calcAmount(pos.getVolume(), pos.getFaceValue());
		BigDecimal bankruptPrice = this.holdPosStrategy.calcBankruptPrice(pos.getLongFlag(), pos.getMeanPrice(), _amount, pos.getPosMargin());
		if(IntBool.isTrue(pos.getLongFlag())){
			if(BigUtils.lt(price, bankruptPrice)){
				throw new ExException(BBOrderError.BANKRUPT_PRICE);
			}
		}else{
			if(BigUtils.gt(price, bankruptPrice)){
				throw new ExException(BBOrderError.BANKRUPT_PRICE);
			}
		}
	}

	private boolean existClientOrderId(long userId, String clientOrderId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("clientOrderId", clientOrderId);
		Long count = this.orderQueryService.queryCount(params);
		return count>0;
	}

	private void setOther(BBOrder bBOrder, BBPosition pos){
		bBOrder.setFeeCost(BigDecimal.ZERO);
		bBOrder.setFilledVolume(BigDecimal.ZERO);
		bBOrder.setCancelVolume(null);
		if(bBOrder.getCloseFlag()==OrderFlag.ACTION_CLOSE){
			bBOrder.setClosePosId(pos.getId());
		}
		bBOrder.setTriggerFlag(IntBool.NO);
		bBOrder.setCancelTime(null);

		////////////log////////////
		bBOrder.setCreateOperator(null);
		bBOrder.setCancelOperator(null);
		bBOrder.setRemark(null);
		
		///////////其他///
		bBOrder.setCancelVolume(BigDecimal.ZERO);
	}
	
	private void cutBalance(Long userId, String asset, Long orderId, BigDecimal amount, int longFlag){
		BBCutRequest request = new BBCutRequest();
		request.setAmount(amount);
		request.setAsset(asset);
		request.setRemark("开仓");
		request.setTradeNo(SnUtils.getOrderPaySn(""+orderId));
		request.setTradeType(IntBool.isTrue(longFlag)?BBAccountTradeType.ORDER_OPEN_LONG:BBAccountTradeType.ORDER_CLOSE_SHORT);
		request.setUserId(userId);
		request.setAssociatedId(orderId);
		this.bBAccountCoreService.cut(request);
	}
	
	private Integer returnCancelAmt(Long userId, String asset, Long orderId, BigDecimal amount){
		BBAddRequest request = new BBAddRequest();
		request.setAmount(amount);
		request.setAsset(asset);
		request.setRemark("撤单还余额");
		request.setTradeNo(SnUtils.getCancelOrderReturnSn(""+orderId));
		SnUtils.getSynchReturnSn(""+orderId);
		request.setTradeType(BBAccountTradeType.ORDER_CANCEL);
		request.setUserId(userId);
		request.setAssociatedId(orderId);
		return this.bBAccountCoreService.add(request);
	}

	//设置平仓订单的各种费率，平仓订单
	private void setCloseOrderFee(BBOrder bBOrder) {
		bBOrder.setMarginRatio(BigDecimal.ZERO);
		bBOrder.setOpenFeeRatio(BigDecimal.ZERO);
		bBOrder.setCloseFeeRatio(BigDecimal.ZERO);
		
		bBOrder.setOrderMargin(BigDecimal.ZERO);
		bBOrder.setOpenFee(BigDecimal.ZERO);
		bBOrder.setCloseFee(BigDecimal.ZERO);
		bBOrder.setGrossMargin(BigDecimal.ZERO);
	}

	//设置开仓订单的各种费率
	private void setOpenOrderFee(BBOrder bBOrder) {
		bBOrder.setMarginRatio(feeRatioService.getInitedMarginRatio(bBOrder.getLeverage()));
		bBOrder.setOpenFeeRatio(feeRatioService.getOpenFeeRatio(bBOrder.getUserId(), bBOrder.getAsset(), bBOrder.getSymbol()));
		bBOrder.setCloseFeeRatio(feeRatioService.getCloseFeeRatio(bBOrder.getUserId(), bBOrder.getAsset(), bBOrder.getSymbol()));
		
		OrderRatioData ratioData = orderStrategy.calcOrderAmt(bBOrder);
		
		bBOrder.setOpenFee(ratioData.getOpenFee());
		bBOrder.setCloseFee(ratioData.getCloseFee());
		bBOrder.setOrderMargin(ratioData.getGrossMargin());
		bBOrder.setGrossMargin(ratioData.getGrossMargin());
	}
	
	@LockIt(key="${userId}-${asset}-${symbol}")
	public void setUserCancel(long userId, String asset, String symbol, long orderId){
		
		BBOrder order = this.orderQueryService.getOrder(userId, orderId);
		
		if(!this.canCancel(order, orderId)){
			logger.info("订单无法取消：{}", order);
			return;
		}
		
		if(order.getCloseFlag()==OrderFlag.ACTION_CLOSE){
			BBPosition pos = this.positionDataService.getPosition(userId, asset, symbol, order.getClosePosId());
			this.checkLiqStatus(pos);	
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
	public void cancel(long userId, String asset, String symbol, long orderId, BigDecimal number){
		this.doCancel(userId, asset, symbol, orderId, number);
	}
	
	public void cance4Liq(long userId, String asset, String symbol, long orderId, BigDecimal number){
		this.doCancel(userId, asset, symbol, orderId, number);
	}
	
	/**
	 * 撤销委托
	 * @param userId
	 * @param asset
	 * @param orderId 订单ID
	 * @param number 撤几张合约
	 */
	private void doCancel(long userId, String asset, String symbol, long orderId, BigDecimal number){
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
		
		if(order.getCloseFlag()==OrderFlag.ACTION_OPEN){
			//返还余额
			if(number!=null && BigUtils.ne(remaining, number)){
				logger.warn("取消数量不一致：{},{}", remaining, number);
			}
			
			OrderRatioData ratioData = orderStrategy.calcRaitoAmt(order, remaining);
			
			BigDecimal cancelledGrossFee = ratioData.getGrossMargin();
			
			int result = this.returnCancelAmt(userId, asset, orderId, cancelledGrossFee);
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
		
		//清空押金
		order.setOrderMargin(BigDecimal.ZERO);
		order.setOpenFee(BigDecimal.ZERO);
		order.setCloseFee(BigDecimal.ZERO);
		
		this.orderUpdateService.updateOrder(order, now);
		
		return;
	}

	@LockIt(key="${userId}-${asset}-${symbol}")
	public void setNewStatus(long userId, String asset, String symbol, long orderId){
		long now = DbDateUtils.now();
		this.orderUpdateService.setNewStatus(orderId, userId, OrderStatus.NEW, OrderStatus.PENDING_NEW, now);
	}

	/*
	 * 检查可平仓位
	 */
	private void checkClosablePosition(BBPosition pos, BigDecimal number) {
		if(pos==null){
			throw new ExException(BBPositonError.POS_NOT_ENOUGH);
		}
		BigDecimal closablePos = orderQueryService.getClosingVolume(pos);
        //判断可平仓位是否足够
        if (BigUtils.gt(number, closablePos)) {
            throw new ExException(BBPositonError.POS_NOT_ENOUGH);
        }
	}
	
}
