package com.hp.sh.expv3.pc.module.order.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.constant.InvokeResult;
import com.hp.sh.expv3.pc.component.FaceValueQuery;
import com.hp.sh.expv3.pc.constant.MarginMode;
import com.hp.sh.expv3.pc.constant.PcAccountTradeType;
import com.hp.sh.expv3.pc.constant.PcOrderType;
import com.hp.sh.expv3.pc.error.OrderError;
import com.hp.sh.expv3.pc.module.account.api.request.CutMoneyRequest;
import com.hp.sh.expv3.pc.module.account.service.impl.PcAccountCoreService;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderDAO;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.position.dao.PcPositionDAO;
import com.hp.sh.expv3.pc.module.symbol.entity.PcAccountSymbol;
import com.hp.sh.expv3.pc.module.symbol.service.PcAccountSymbolService;
import com.hp.sh.expv3.utils.IntBool;

/**
 * 委托
 * @author wangjg
 *
 */
@Service
@Transactional
public class PcOrderService {
	private static final Logger logger = LoggerFactory.getLogger(PcOrderService.class);
	
	@Autowired
	private PcAccountSymbolService pcAccountSymbolService;

	@Autowired
	private PcOrderDAO pcOrderDAO;
	
	@Autowired
	private MarginRatioService marginRatioService;
	
	@Autowired
	private PcAccountCoreService pcAccountCoreService;
	
	@Autowired
	private FaceValueQuery faceValueQuery;

	@Autowired
	private PcPositionDAO pcPositionDAO;
	
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
	public PcOrder create(long userId, String cliOrderId, String asset, String symbol, int closeFlag, int longFlag, int timeInForce, BigDecimal price, BigDecimal number){
		
		if(this.existClientOrderId(userId, cliOrderId)){
			throw new ExException(OrderError.CREATED);
		}
		
		Date now = new Date();
		PcOrder pcOrder = new PcOrder();
		
		pcOrder.setUserId(userId);
		pcOrder.setCreated(now);
		pcOrder.setModified(now);
		
		pcOrder.setAsset(asset);
		pcOrder.setSymbol(symbol);
		pcOrder.setCloseFlag(closeFlag);
		pcOrder.setLongFlag(longFlag);
		pcOrder.setLeverage(this.getLeverage(userId, asset, symbol, IntBool.isTrue(longFlag)));
		pcOrder.setPrice(price);
		pcOrder.setVolume(number);
		pcOrder.setFaceValue(faceValueQuery.get(pcOrder.getSymbol()));
		pcOrder.setOrderType(PcOrderType.LIMIT);
		pcOrder.setMarginMode(MarginMode.FIXED);
		pcOrder.setTimeInForce(timeInForce);
		pcOrder.setClientOrderId(cliOrderId);
		
		/////////押金设置/////////
		
		if (IntBool.isTrue(closeFlag)) {
			this.setCloseOrderFee(pcOrder);
		}else{
			this.setOpenOrderFee(pcOrder);
		}
		
		////////其他字段，后面随状态修改////////
		this.setOther(pcOrder);
		
		pcOrderDAO.save(pcOrder);

		//押金扣除
		this.cutBalance(userId, asset, pcOrder.getId(), pcOrder.getGrossMargin());
		
		return pcOrder;
	}
	
	private boolean existClientOrderId(long userId, String clientOrderId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("clientOrderId", clientOrderId);
		Long count = this.pcOrderDAO.queryCount(params);
		return count>0;
	}

	private void setOther(PcOrder pcOrder){
		pcOrder.setFeeCost(BigDecimal.ZERO);
		pcOrder.setFilledVolume(BigDecimal.ZERO);
		pcOrder.setCancelVolume(null);
		pcOrder.setClosePosId(null);
		pcOrder.setTriggerFlag(IntBool.NO);
		pcOrder.setCancelTime(null);
		pcOrder.setStatus(PcOrder.PENDING_NEW);

		////////////强平////////////
		
		pcOrder.setVisibleFlag(IntBool.YES);
		pcOrder.setActiveFlag(IntBool.YES);
		
		////////////log////////////
		pcOrder.setCreateOperator(null);
		pcOrder.setCancelOperator(null);
		pcOrder.setRemark(null);
		
		///////////其他///
		pcOrder.setCancelVolume(BigDecimal.ZERO);
	}
	
	private void cutBalance(Long userId, String asset, Long orderId, BigDecimal amount){
		CutMoneyRequest request = new CutMoneyRequest();
		request.setAmount(amount);
		request.setAsset(asset);
		request.setRemark("开仓扣除");
		request.setTradeNo("O"+orderId);
		request.setTradeType(PcAccountTradeType.ORDER_OPEN);
		request.setUserId(userId);
		request.setAssociatedId(orderId);
		this.pcAccountCoreService.cut(request);
	}
	
	private Integer returnCancelAmt(Long userId, String asset, Long orderId, BigDecimal amount){
		CutMoneyRequest request = new CutMoneyRequest();
		request.setAmount(amount);
		request.setAsset(asset);
		request.setRemark("撤单还余额");
		request.setTradeNo("C"+orderId);
		request.setTradeType(PcAccountTradeType.ORDER_CANCEL);
		request.setUserId(userId);
		request.setAssociatedId(orderId);
		return this.pcAccountCoreService.add(request);
	}

	//设置平仓订单的各种费率
	private void setCloseOrderFee(PcOrder pcOrder) {
        //判断可平仓位是否足够
        if (pcOrder.getVolume().compareTo(this.getClosablePos(pcOrder.getUserId(), pcOrder.getAsset(), pcOrder.getSymbol()))>0) {
            throw new ExException(OrderError.POS_NOT_ENOUGH);
        }
        
		pcOrder.setMarginRatio(BigDecimal.ZERO);
		pcOrder.setOpenFeeRatio(BigDecimal.ZERO);
		pcOrder.setCloseFeeRatio(BigDecimal.ZERO);
		
		pcOrder.setOrderMargin(BigDecimal.ZERO);
		pcOrder.setOpenFee(BigDecimal.ZERO);
		pcOrder.setCloseFee(BigDecimal.ZERO);
		pcOrder.setGrossMargin(BigDecimal.ZERO);
	}
	
	public BigDecimal getClosablePos(Long userId, String asset, String symbol){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("asset", asset);
		params.put("symbol", symbol);
		params.put("SUM", "volume");
		BigDecimal amount = this.pcPositionDAO.queryAmount(params);
		if(amount==null){
			return BigDecimal.ZERO;
		}
		return amount;
	}

	//设置开仓订单的各种费率
	private void setOpenOrderFee(PcOrder pcOrder) {
		pcOrder.setMarginRatio(marginRatioService.getInitedMarginRatio(pcOrder.getLeverage()));
		pcOrder.setOpenFeeRatio(marginRatioService.getOpenFeeRatio(pcOrder.getUserId()));
		pcOrder.setCloseFeeRatio(marginRatioService.getCloseFeeRatio(pcOrder.getUserId()));
		
		//交易金额
		BigDecimal amount = pcOrder.getVolume().multiply(pcOrder.getFaceValue());
		
		BigDecimal baseValue = BaseValueCalc.calcValue(amount, pcOrder.getPrice());
		
		BigDecimal orderMargin = MarginFeeCalc.calMargin(baseValue, pcOrder.getMarginRatio());
		pcOrder.setOrderMargin(orderMargin);
		
		BigDecimal openFee = MarginFeeCalc.calcFee(baseValue, pcOrder.getOpenFeeRatio());
		pcOrder.setOpenFee(openFee);

		BigDecimal closeFee = MarginFeeCalc.calcFee(baseValue, pcOrder.getCloseFeeRatio());
		pcOrder.setCloseFee(closeFee);
		
		pcOrder.setGrossMargin(closeFee.add(openFee).add(orderMargin));
	}

	private BigDecimal getLeverage(long userId, String asset, String symbol, boolean isLong){
		PcAccountSymbol as = pcAccountSymbolService.get(userId, asset, symbol);
		if(isLong){	//多
			return as.getLongLeverage();
		}else{		//空
			return as.getShortLeverage();
		}
	}
	
	public PcOrder getOrder(Long userId, Long orderId){
		return this.pcOrderDAO.findById(userId, orderId);
	}
	
	public void cancel(long userId, String asset, long orderId, BigDecimal cancelAmt){
		//返还余额
		int result = this.returnCancelAmt(userId, asset, orderId, cancelAmt);
		if(result==InvokeResult.NOCHANGE){
			//利用合约账户的幂等性实现本方法的幂等性
			logger.warn("已经执行过了");
			return;
		}

		//修改订单状态（撤销）
		Date now = new Date();
//		this.setCancelStatus(userId, asset, orderId, PcOrder.CANCELED);
		PcOrder order = this.pcOrderDAO.findById(userId, orderId);
		order.setCancelTime(now);
		order.setCancelVolume(cancelAmt);
		order.setActiveFlag(PcOrder.NO);
		order.setStatus(PcOrder.CANCELED);
		this.pcOrderDAO.update(order);
		
		return;
	}
	
	public void setCancelStatus(long userId, String asset, long orderId, Integer cancelStatsus){
		Date now = new Date();
		
		long count = this.pcOrderDAO.cancelOrder(userId, orderId, cancelStatsus, now);
		
		if(count!=1){
			throw new RuntimeException("更新失败，更新行数："+count);
		}
        
	}
	
	public void changeStatus(Long userId, Long orderId, Integer newStatus, Integer desiredOldStatus){
		long count = this.pcOrderDAO.changeStatus(userId, orderId, newStatus, desiredOldStatus, new Date());
		if(count!=1){
			throw new RuntimeException("更新失败，更新行数："+count);
		}
	}

}
