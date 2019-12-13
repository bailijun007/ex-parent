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
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.constant.InvokeResult;
import com.hp.sh.expv3.pc.api.request.AddMoneyRequest;
import com.hp.sh.expv3.pc.api.request.CutMoneyRequest;
import com.hp.sh.expv3.pc.component.FeeRatioService;
import com.hp.sh.expv3.pc.constant.MarginMode;
import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.constant.PcAccountTradeType;
import com.hp.sh.expv3.pc.constant.PcOrderType;
import com.hp.sh.expv3.pc.error.OrderError;
import com.hp.sh.expv3.pc.error.PositonError;
import com.hp.sh.expv3.pc.module.account.service.PcAccountCoreService;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderDAO;
import com.hp.sh.expv3.pc.module.order.entity.OrderStatus;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.position.service.PcPositionService;
import com.hp.sh.expv3.pc.module.symbol.service.PcAccountSymbolService;
import com.hp.sh.expv3.pc.strategy.aabb.AABBMetadataService;
import com.hp.sh.expv3.pc.strategy.common.CommonOrderStrategy;
import com.hp.sh.expv3.pc.strategy.vo.OrderRatioData;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.BigMathUtils;

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
	private PcAccountSymbolService pcSymbolService;

	@Autowired
	private PcOrderDAO pcOrderDAO;
	
	@Autowired
	private FeeRatioService feeRatioService;
	
	@Autowired
	private PcAccountCoreService pcAccountCoreService;
	
	@Autowired
	private AABBMetadataService metadataService;
	
	@Autowired
	private CommonOrderStrategy orderStrategy;	
	
	@Autowired
	private PcPositionService pcPositionService;

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
	public PcOrder create(long userId, String cliOrderId, String asset, String symbol, int closeFlag, int longFlag, int timeInForce, BigDecimal price, BigDecimal number){
		
//		if(this.existClientOrderId(userId, cliOrderId)){
//			throw new ExException(OrderError.CREATED);
//		}
		
		//check 检查可平仓位
		checkClosablePosition(userId, asset, symbol, number, closeFlag, longFlag);
		
		Date now = new Date();
		
		
		//订单基本数据
		PcOrder pcOrder = new PcOrder();
		
		pcOrder.setAsset(asset);
		pcOrder.setSymbol(symbol);
		pcOrder.setCloseFlag(closeFlag);
		pcOrder.setLongFlag(longFlag);
		pcOrder.setLeverage(pcSymbolService.getLeverage(userId, asset, symbol, longFlag));
		pcOrder.setVolume(number);
		pcOrder.setFaceValue(metadataService.getFaceValue(pcOrder.getSymbol()));
		pcOrder.setPrice(price);
		
		pcOrder.setOrderType(PcOrderType.LIMIT);
		pcOrder.setMarginMode(MarginMode.FIXED);
		pcOrder.setTimeInForce(timeInForce);
		pcOrder.setUserId(userId);
		pcOrder.setCreated(now);
		pcOrder.setModified(now);
		pcOrder.setStatus(OrderStatus.PENDING_NEW);
		pcOrder.setActiveFlag(IntBool.YES);
		pcOrder.setClientOrderId(cliOrderId);
		
		/////////押金数据/////////
		
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

		////////////强平////////////
		
		pcOrder.setVisibleFlag(IntBool.YES);
		
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
		AddMoneyRequest request = new AddMoneyRequest();
		request.setAmount(amount);
		request.setAsset(asset);
		request.setRemark("撤单还余额");
		request.setTradeNo("C"+orderId);
		request.setTradeType(PcAccountTradeType.ORDER_CANCEL);
		request.setUserId(userId);
		request.setAssociatedId(orderId);
		return this.pcAccountCoreService.add(request);
	}

	//设置平仓订单的各种费率，平仓订单
	private void setCloseOrderFee(PcOrder pcOrder) {
		pcOrder.setMarginRatio(BigDecimal.ZERO);
		pcOrder.setOpenFeeRatio(BigDecimal.ZERO);
		pcOrder.setCloseFeeRatio(BigDecimal.ZERO);
		
		pcOrder.setOrderMargin(BigDecimal.ZERO);
		pcOrder.setOpenFee(BigDecimal.ZERO);
		pcOrder.setCloseFee(BigDecimal.ZERO);
		pcOrder.setGrossMargin(BigDecimal.ZERO);
	}

	//设置开仓订单的各种费率
	private void setOpenOrderFee(PcOrder pcOrder) {
		pcOrder.setMarginRatio(feeRatioService.getInitedMarginRatio(pcOrder.getLeverage()));
		pcOrder.setOpenFeeRatio(feeRatioService.getOpenFeeRatio(pcOrder.getUserId()));
		pcOrder.setCloseFeeRatio(feeRatioService.getCloseFeeRatio(pcOrder.getUserId()));
		
		OrderRatioData ratioData = orderStrategy.calcOrderAmt(pcOrder);
		pcOrder.setOpenFee(ratioData.getOpenFee());
		pcOrder.setCloseFee(ratioData.getCloseFee());
		pcOrder.setOrderMargin(ratioData.getGrossMargin());
		pcOrder.setGrossMargin(ratioData.getGrossMargin());
	}
	
	@LockIt(key="${userId}-${asset}-${symbol}")
	public void setCancelStatus(long userId, String asset, String symbol, long orderId, Integer cancelStatsus){
		Date now = new Date();
		
		PcOrder order = this.pcOrderDAO.findById(userId, orderId);
		if(order.getStatus() == OrderStatus.CANCELED){
			throw new ExException(OrderError.CANCELED);
		}
		if(BigMathUtils.eq(order.getVolume(), order.getFilledVolume())){
			throw new ExException(OrderError.FILLED);
		}
		
		long count = this.pcOrderDAO.setCancelStatus(userId, orderId, cancelStatsus, now);
		
		if(count!=1){
			throw new RuntimeException("更新失败，更新行数："+count);
		}
        
	}
	
	/**
	 * 
	 * @param userId
	 * @param asset
	 * @param orderId 订单ID
	 * @param number 撤几张合约
	 */
	@LockIt(key="${userId}-${asset}-${symbol}")
	public void cancel(long userId, String asset, String symbol, long orderId, BigDecimal number){
			//返还余额
	
			PcOrder order = this.pcOrderDAO.findById(userId, orderId);
			
			OrderRatioData ratioData = orderStrategy.calcRaitoAmt(order, number);
			
			BigDecimal cancelledGrossFee = ratioData.getGrossMargin();
			
			int result = this.returnCancelAmt(userId, asset, orderId, cancelledGrossFee);
			if(result==InvokeResult.NOCHANGE){
				//利用合约账户的幂等性实现本方法的幂等性
				logger.warn("已经执行过了");
				return;
			}
	
			//修改订单状态（撤销）
			Date now = new Date();
	//		this.setCancelStatus(userId, asset, orderId, PcOrder.CANCELED);
			order.setCancelTime(now);
			order.setCancelVolume(number);
			order.setActiveFlag(PcOrder.NO);
			order.setStatus(OrderStatus.CANCELED);
			this.pcOrderDAO.update(order);
			
			return;
		}

	public void changeStatus(Long userId, Long orderId, Integer newStatus, Integer desiredOldStatus){
		long count = this.pcOrderDAO.changeStatus(userId, orderId, newStatus, desiredOldStatus, new Date());
		if(count!=1){
			throw new RuntimeException("更新失败，更新行数："+count);
		}
	}

	private void checkClosablePosition(long userId, String asset, String symbol, BigDecimal volume, int closeFlag, int longFlag) {
		if(closeFlag!=OrderFlag.ACTION_CLOSE){
			return;
		}
		
		PcPosition pos = this.pcPositionService.getCurrentPosition(userId, asset, symbol, longFlag);
		if(pos==null){
			throw new ExException(PositonError.POS_NOT_ENOUGH);
		}
		
		BigDecimal cpv = this.pcOrderDAO.getClosedPosVolume(userId, pos.getId());
		
		BigDecimal availablePos = pos.getVolume().subtract(cpv);
		
        //判断可平仓位是否足够
        if (availablePos.compareTo(BigDecimal.ZERO)>0) {
            throw new ExException(PositonError.POS_NOT_ENOUGH);
        }
	}
	
}
