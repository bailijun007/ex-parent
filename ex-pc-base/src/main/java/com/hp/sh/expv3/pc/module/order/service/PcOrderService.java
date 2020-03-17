package com.hp.sh.expv3.pc.module.order.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.exception.ExSysException;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.constant.InvokeResult;
import com.hp.sh.expv3.pc.component.FeeRatioService;
import com.hp.sh.expv3.pc.component.MetadataService;
import com.hp.sh.expv3.pc.constant.LiqStatus;
import com.hp.sh.expv3.pc.constant.MarginMode;
import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.constant.OrderStatus;
import com.hp.sh.expv3.pc.constant.PcAccountTradeType;
import com.hp.sh.expv3.pc.constant.PcOrderType;
import com.hp.sh.expv3.pc.constant.TimeInForce;
import com.hp.sh.expv3.pc.error.PcOrderError;
import com.hp.sh.expv3.pc.error.PcPositonError;
import com.hp.sh.expv3.pc.module.account.service.PcAccountCoreService;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.position.service.PcPositionDataService;
import com.hp.sh.expv3.pc.module.symbol.service.PcAccountSymbolService;
import com.hp.sh.expv3.pc.strategy.PcStrategyContext;
import com.hp.sh.expv3.pc.strategy.vo.OrderFeeData;
import com.hp.sh.expv3.pc.vo.request.PcAddRequest;
import com.hp.sh.expv3.pc.vo.request.PcCutRequest;
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
public class PcOrderService {
	private static final Logger logger = LoggerFactory.getLogger(PcOrderService.class);
	
	@Autowired
	private PcAccountSymbolService symbolService;

	@Autowired
	private PcOrderUpdateService orderUpdateService;
	
	@Autowired
	private PcOrderQueryService orderQueryService;
	
	@Autowired
	private FeeRatioService feeRatioService;
	
	@Autowired
	private PcAccountCoreService accountCoreService;
	
	@Autowired
	private MetadataService metadataService;
	
	@Autowired
	private PcPositionDataService positionDataService;
	
    @Autowired
    private PcStrategyContext strategyContext;
	
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
	public PcOrder create(long userId, String clientOrderId, String asset, String symbol, int closeFlag, int longFlag, int timeInForce, BigDecimal price, BigDecimal number){
		PcPosition pos = this.positionDataService.getCurrentPosition(userId, asset, symbol, longFlag);
		if(closeFlag==OrderFlag.ACTION_OPEN){
			return this.createOpenOrder(userId, clientOrderId, asset, symbol, longFlag, timeInForce, price, number, pos, IntBool.YES);
		}else{
			return this.createCloseOrder(userId, clientOrderId, asset, symbol, longFlag, timeInForce, price, number, pos, IntBool.YES, IntBool.NO);
		}
	}
	
	public PcOrder createLiqOrder(long userId, String clientOrderId, String asset, String symbol, int longFlag, BigDecimal price, BigDecimal number, PcPosition pos){
		return this.createCloseOrder(userId, clientOrderId, asset, symbol, longFlag, TimeInForce.IMMEDIATE_OR_CANCEL, price, number, pos, IntBool.NO, IntBool.YES);
	}
	
	//创建开仓委托
	protected PcOrder createOpenOrder(long userId, String clientOrderId, String asset, String symbol, int longFlag, int timeInForce, BigDecimal price, BigDecimal number, PcPosition pos, Integer visibleFlag){

		this.checkClientOrderId(userId, clientOrderId);
		
		Long now = DbDateUtils.now();
		
		//订单基本数据
		PcOrder pcOrder = new PcOrder();
		
		pcOrder.setAsset(asset);
		pcOrder.setSymbol(symbol);
		pcOrder.setCloseFlag(OrderFlag.ACTION_OPEN);
		pcOrder.setLongFlag(longFlag);
		pcOrder.setLeverage(symbolService.getLeverage(userId, asset, symbol, longFlag));
		pcOrder.setVolume(number);
		pcOrder.setFaceValue(metadataService.getFaceValue(asset, symbol));
		pcOrder.setPrice(price);
		
		pcOrder.setOrderType(PcOrderType.MARKET);
		pcOrder.setMarginMode(MarginMode.FIXED);
		pcOrder.setTimeInForce(timeInForce);
		pcOrder.setUserId(userId);
		pcOrder.setCreated(now);
		pcOrder.setModified(now);
		pcOrder.setStatus(OrderStatus.PENDING_NEW);
		pcOrder.setClientOrderId(clientOrderId);
		pcOrder.setActiveFlag(IntBool.YES);
		pcOrder.setLiqFlag(IntBool.NO);
		
		//设置开仓订单的各种费率
		pcOrder.setMarginRatio(feeRatioService.getInitedMarginRatio(pcOrder.getLeverage()));
		pcOrder.setOpenFeeRatio(feeRatioService.getTakerFeeRatio(pcOrder.getUserId(), pcOrder.getAsset(), pcOrder.getSymbol()));
		pcOrder.setCloseFeeRatio(feeRatioService.getTakerFeeRatio(pcOrder.getUserId(), pcOrder.getAsset(), pcOrder.getSymbol()));
		
		OrderFeeData feeData = strategyContext.calcNewOrderFee(pcOrder.getAsset(), pcOrder.getSymbol(), pcOrder);
		pcOrder.setOpenFee(feeData.getOpenFee());
		pcOrder.setCloseFee(feeData.getCloseFee());
		pcOrder.setOrderMargin(feeData.getOrderMargin());
		pcOrder.setGrossMargin(feeData.getGrossMargin());
		
		////////其他字段，后面随状态修改////////
		this.setOther(pcOrder, pos);
		
		pcOrder.setVisibleFlag(visibleFlag);
		
		this.orderUpdateService.saveOrder(pcOrder);

		//开仓押金扣除
		this.cutBalance(userId, asset, pcOrder.getId(), pcOrder.getGrossMargin(), longFlag);
		
		return pcOrder;
	}
	
	//平仓委托
	protected PcOrder createCloseOrder(long userId, String clientOrderId, String asset, String symbol, int longFlag, int timeInForce, BigDecimal price, BigDecimal number, PcPosition pos, Integer visibleFlag, int liqFlag){
		
		this.checkClientOrderId(userId, clientOrderId);
		
		//非强平委托
		if(IntBool.isFalse(liqFlag)){
			this.checkLiqStatus(pos);
			// 检查可平仓位
			this.checkClosablePosition(pos, number);
			// 检查价格
			this.checkPrice(pos, price);
		}
		
		Long now = DbDateUtils.now();
		
		//订单基本数据
		PcOrder pcOrder = new PcOrder();
		
		pcOrder.setAsset(asset);
		pcOrder.setSymbol(symbol);
		pcOrder.setCloseFlag(OrderFlag.ACTION_CLOSE);
		pcOrder.setLongFlag(longFlag);
		pcOrder.setLeverage(symbolService.getLeverage(userId, asset, symbol, longFlag));
		pcOrder.setVolume(number);
		pcOrder.setFaceValue(metadataService.getFaceValue(asset, symbol));
		pcOrder.setPrice(price);
		
		pcOrder.setOrderType(PcOrderType.LIMIT);
		pcOrder.setMarginMode(MarginMode.FIXED);
		pcOrder.setTimeInForce(timeInForce);
		pcOrder.setUserId(userId);
		pcOrder.setCreated(now);
		pcOrder.setModified(now);
		pcOrder.setStatus(OrderStatus.PENDING_NEW);
		pcOrder.setClientOrderId(clientOrderId);
		pcOrder.setActiveFlag(IntBool.YES);
		pcOrder.setLiqFlag(liqFlag);
		
		/////////押金数据/////////

		//设置平仓订单的各种费率，平仓订单
		pcOrder.setMarginRatio(BigDecimal.ZERO);
		pcOrder.setOpenFeeRatio(BigDecimal.ZERO);
		pcOrder.setCloseFeeRatio(BigDecimal.ZERO);
		
		pcOrder.setOrderMargin(BigDecimal.ZERO);
		pcOrder.setOpenFee(BigDecimal.ZERO);
		pcOrder.setCloseFee(BigDecimal.ZERO);
		pcOrder.setGrossMargin(BigDecimal.ZERO);
		
		////////其他字段，后面随状态修改////////
		this.setOther(pcOrder, pos);
		
		pcOrder.setVisibleFlag(visibleFlag);
		
		this.orderUpdateService.saveOrder(pcOrder);
		
		return pcOrder;
	}
	
	private void checkLiqStatus(PcPosition pos) {
		if(pos==null){
			return;
		}
		if(pos.getLiqStatus() == LiqStatus.FROZEN){
			throw new ExSysException(PcPositonError.LIQING);
		}
		if(pos.getLiqStatus() == LiqStatus.FORCE_CLOSE){
			throw new ExException(PcPositonError.FORCE_CLOSE);
		}
	}

	private void checkPrice(PcPosition pos, BigDecimal price) {
		BigDecimal bankruptPrice = this.strategyContext.calcBankruptPrice(pos);
		if(IntBool.isTrue(pos.getLongFlag())){
			if(BigUtils.lt(price, bankruptPrice)){
				throw new ExException(PcOrderError.BANKRUPT_PRICE);
			}
		}else{
			if(BigUtils.gt(price, bankruptPrice)){
				throw new ExException(PcOrderError.BANKRUPT_PRICE);
			}
		}
	}
	

	private void checkClientOrderId(long userId, String clientOrderId) {
		if(this.existClientOrderId(userId, clientOrderId)){
			//throw new ExException(PcOrderError.CREATED);
		}
	}

	private boolean existClientOrderId(long userId, String clientOrderId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("clientOrderId", clientOrderId);
		Long count = this.orderQueryService.queryCount(params);
		return count>0;
	}

	private void setOther(PcOrder pcOrder, PcPosition pos){
		pcOrder.setFeeCost(BigDecimal.ZERO);
		pcOrder.setFilledVolume(BigDecimal.ZERO);
		pcOrder.setCancelVolume(null);
		if(pcOrder.getCloseFlag()==OrderFlag.ACTION_CLOSE){
			pcOrder.setClosePosId(pos.getId());
		}
		pcOrder.setTriggerFlag(IntBool.NO);
		pcOrder.setCancelTime(null);

		////////////log////////////
		pcOrder.setCreateOperator(null);
		pcOrder.setCancelOperator(null);
		pcOrder.setRemark(null);
		
		pcOrder.setVersion(0L);
		
		///////////其他///
		pcOrder.setCancelVolume(BigDecimal.ZERO);
	}
	
	private void cutBalance(Long userId, String asset, Long orderId, BigDecimal amount, int longFlag){
		PcCutRequest request = new PcCutRequest();
		request.setAmount(amount);
		request.setAsset(asset);
		request.setRemark("开仓");
		request.setTradeNo(SnUtils.getOrderPaySn(""+orderId));
		request.setTradeType(IntBool.isTrue(longFlag)?PcAccountTradeType.ORDER_OPEN_LONG:PcAccountTradeType.ORDER_CLOSE_SHORT);
		request.setUserId(userId);
		request.setAssociatedId(orderId);
		this.accountCoreService.cut(request);
	}
	
	private Integer returnCancelAmt(Long userId, String asset, Long orderId, BigDecimal orderMargin, BigDecimal openFee, BigDecimal closeFee){
		PcAddRequest request = new PcAddRequest();
		request.setAmount(orderMargin.add(openFee).add(closeFee));
		request.setAsset(asset);
		request.setRemark(BigFormat.format("撤单还押金：%s,%s,%s", orderMargin, openFee, closeFee));
		request.setTradeNo(SnUtils.getCancelOrderReturnSn(""+orderId));
		request.setTradeType(PcAccountTradeType.ORDER_CANCEL);
		request.setUserId(userId);
		request.setAssociatedId(orderId);
		return this.accountCoreService.add(request);
	}
	
	@LockIt(key="${userId}-${asset}-${symbol}")
	public boolean setPendingCancel(long userId, String asset, String symbol, long orderId){
		
		PcOrder order = this.orderQueryService.getOrder(userId, orderId);
		
		if(!this.canCancel(order, orderId)){
			logger.info("订单无法取消：{}", order);
			return false;
		}
		
		if(order.getCloseFlag()==OrderFlag.ACTION_CLOSE){
			PcPosition pos = this.positionDataService.getPosition(userId, asset, symbol, order.getClosePosId());
			this.checkLiqStatus(pos);	
		}
		
		Long now = DbDateUtils.now();
		this.orderUpdateService.setPendingCancelStatus(now, orderId, userId, order.getVersion());
		
		return true;
	}
	
	@LockIt(key="${userId}-${asset}-${symbol}")
	public void setNewStatus(long userId, String asset, String symbol, long orderId){
		PcOrder order = this.orderQueryService.getOrder(userId, orderId);
		
		if(order.getStatus()!=OrderStatus.PENDING_NEW){
			logger.error("NEW状态错误，orderId={}", orderId);
			return ;
		}

		long now = DbDateUtils.now();
		
		this.orderUpdateService.setNewStatus(order, now);
	}

	private boolean canCancel(PcOrder order, Long orderId){
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
		PcOrder order = this.orderQueryService.getOrder(userId, orderId);
		if(order==null){
			logger.error("订单不存在:orderId={}", orderId);
			return;
		}
		if(order.getStatus() == OrderStatus.CANCELED){
			logger.warn("订单已经是取消状态了");
			return;
		}
		if(order.getStatus() == OrderStatus.FILLED){
			logger.warn("订单已经是完成状态了");
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
			
			int result = this.returnCancelAmt(userId, asset, orderId, order.getOrderMargin(), order.getOpenFee(), order.getCloseFee());
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
		order.setActiveFlag(PcOrder.NO);
		order.setStatus(OrderStatus.CANCELED);
		
		//清空押金
		order.setOrderMargin(BigDecimal.ZERO);
		order.setOpenFee(BigDecimal.ZERO);
		order.setCloseFee(BigDecimal.ZERO);
		
		this.orderUpdateService.updateOrder(order, now);
		
		return;
	}

	/*
	 * 检查可平仓位
	 */
	private void checkClosablePosition(PcPosition pos, BigDecimal number) {
		if(pos==null){
			throw new ExException(PcPositonError.POS_NOT_ENOUGH);
		}
		BigDecimal closablePos = orderQueryService.getClosingVolume(pos);
        //判断可平仓位是否足够
        if (BigUtils.gt(number, closablePos)) {
            throw new ExException(PcPositonError.POS_NOT_ENOUGH);
        }
	}
	
}
