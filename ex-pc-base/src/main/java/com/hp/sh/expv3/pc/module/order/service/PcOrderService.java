package com.hp.sh.expv3.pc.module.order.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.constant.InvokeResult;
import com.hp.sh.expv3.dev.CrossDB;
import com.hp.sh.expv3.pc.component.FeeRatioService;
import com.hp.sh.expv3.pc.component.MetadataService;
import com.hp.sh.expv3.pc.constant.LiqStatus;
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
import com.hp.sh.expv3.pc.strategy.common.CommonOrderStrategy;
import com.hp.sh.expv3.pc.strategy.vo.OrderRatioData;
import com.hp.sh.expv3.pc.vo.request.PcAddRequest;
import com.hp.sh.expv3.pc.vo.request.PcCutRequest;
import com.hp.sh.expv3.utils.IntBool;
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
	private PcAccountSymbolService pcSymbolService;

	@Autowired
	private PcOrderDAO pcOrderDAO;
	
	@Autowired
	private FeeRatioService feeRatioService;
	
	@Autowired
	private PcAccountCoreService pcAccountCoreService;
	
	@Autowired
	private MetadataService metadataService;
	
	@Autowired
	private CommonOrderStrategy orderStrategy;	
	
	@Autowired
	private PcPositionService pcPositionService;
	
	@Autowired
	private PcOrderService self;

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
		PcPosition pos = this.pcPositionService.getCurrentPosition(userId, asset, symbol, longFlag);
		return self.create(userId, cliOrderId, asset, symbol, closeFlag, longFlag, timeInForce, price, number, pos, IntBool.YES, IntBool.NO);
	}
	
	@LockIt(key="${userId}-${asset}-${symbol}")
	public PcOrder create(long userId, String cliOrderId, String asset, String symbol, int closeFlag, int longFlag, int timeInForce, BigDecimal price, BigDecimal number, PcPosition pos, Integer visibleFlag, int liqFlag){
		
//		if(this.existClientOrderId(userId, cliOrderId)){
//			throw new ExException(OrderError.CREATED);
//		}
		
		//check 检查可平仓位
		if(closeFlag==OrderFlag.ACTION_CLOSE){
			this.checkClosablePosition(pos, number);
		}
		
		Date now = new Date();
		
		
		//订单基本数据
		PcOrder pcOrder = new PcOrder();
		
		pcOrder.setAsset(asset);
		pcOrder.setSymbol(symbol);
		pcOrder.setCloseFlag(closeFlag);
		pcOrder.setLongFlag(longFlag);
		pcOrder.setLeverage(pcSymbolService.getLeverage(userId, asset, symbol, longFlag));
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
		pcOrder.setClientOrderId(cliOrderId);
		pcOrder.setActiveFlag(IntBool.YES);
		pcOrder.setLiqFlag(liqFlag);
		
		/////////押金数据/////////
		
		if (IntBool.isTrue(closeFlag)) {
			this.setCloseOrderFee(pcOrder);
		}else{
			this.setOpenOrderFee(pcOrder);
		}
		
		////////其他字段，后面随状态修改////////
		this.setOther(pcOrder, pos);
		
		pcOrder.setVisibleFlag(visibleFlag);
		
		pcOrderDAO.save(pcOrder);

		//开仓押金扣除
		if(closeFlag==OrderFlag.ACTION_OPEN){
			this.cutBalance(userId, asset, pcOrder.getId(), pcOrder.getGrossMargin(), longFlag);
		}
		
		return pcOrder;
	}
	
	private boolean existClientOrderId(long userId, String clientOrderId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("clientOrderId", clientOrderId);
		Long count = this.pcOrderDAO.queryCount(params);
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
		
		///////////其他///
		pcOrder.setCancelVolume(BigDecimal.ZERO);
	}
	
	private void cutBalance(Long userId, String asset, Long orderId, BigDecimal amount, int longFlag){
		PcCutRequest request = new PcCutRequest();
		request.setAmount(amount);
		request.setAsset(asset);
		request.setRemark("开仓");
		request.setTradeNo("O"+orderId);
		request.setTradeType(IntBool.isTrue(longFlag)?PcAccountTradeType.ORDER_OPEN_LONG:PcAccountTradeType.ORDER_CLOSE_SHORT);
		request.setUserId(userId);
		request.setAssociatedId(orderId);
		this.pcAccountCoreService.cut(request);
	}
	
	private Integer returnCancelAmt(Long userId, String asset, Long orderId, BigDecimal amount){
		PcAddRequest request = new PcAddRequest();
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
		pcOrder.setOpenFeeRatio(feeRatioService.getOpenFeeRatio(pcOrder.getUserId(), pcOrder.getAsset(), pcOrder.getSymbol()));
		pcOrder.setCloseFeeRatio(feeRatioService.getCloseFeeRatio(pcOrder.getUserId(), pcOrder.getAsset(), pcOrder.getSymbol()));
		
		OrderRatioData ratioData = orderStrategy.calcOrderAmt(pcOrder);
		pcOrder.setOpenFee(ratioData.getOpenFee());
		pcOrder.setCloseFee(ratioData.getCloseFee());
		pcOrder.setOrderMargin(ratioData.getGrossMargin());
		pcOrder.setGrossMargin(ratioData.getGrossMargin());
	}
	
	@LockIt(key="${userId}-${asset}-${symbol}")
	public void setPendingCancel(long userId, String asset, String symbol, long orderId){
		Date now = new Date();
		
		PcOrder order = this.pcOrderDAO.findById(userId, orderId);
		if(order.getStatus() == OrderStatus.CANCELED){
			throw new ExException(OrderError.CANCELED);
		}
		if(BigUtils.eq(order.getVolume(), order.getFilledVolume())){
			throw new ExException(OrderError.FILLED);
		}
		if(IntBool.isFlase(order.getActiveFlag())){
			throw new ExException(OrderError.NOT_ACTIVE);
		}
		
		long count = this.pcOrderDAO.setCancelStatus(userId, orderId, OrderStatus.PENDING_CANCEL, now);
		
		if(count!=1){
			throw new RuntimeException("更新失败，更新行数："+count);
		}
        
	}
	
	/**
	 * 撤销委托
	 * @param userId
	 * @param asset
	 * @param orderId 订单ID
	 * @param number 撤几张合约
	 */
	@LockIt(key="${userId}-${asset}-${symbol}")
	public void cancel(long userId, String asset, String symbol, long orderId, BigDecimal number){
		PcOrder order = this.pcOrderDAO.findById(userId, orderId);
		
		if(order.getCloseFlag()==OrderFlag.ACTION_OPEN){
			//返还余额
			if(BigUtils.ne(order.getVolume().subtract(order.getFilledVolume()), number)){
				throw new ExException(OrderError.CANCELED_NUM_ERR);
			}
			
			OrderRatioData ratioData = orderStrategy.calcRaitoAmt(order, number);
			
			BigDecimal cancelledGrossFee = ratioData.getGrossMargin();
			
			int result = this.returnCancelAmt(userId, asset, orderId, cancelledGrossFee);
			if(result==InvokeResult.NOCHANGE){
				//利用合约账户的幂等性实现本方法的幂等性
				logger.warn("已经执行过了");
				return;
			}
		}
		
		//修改订单状态（撤销）
		Date now = new Date();
		order.setCancelTime(now);
		order.setCancelVolume(number);
		order.setActiveFlag(PcOrder.NO);
		order.setStatus(OrderStatus.CANCELED);
		
		//清空押金
		order.setOrderMargin(BigDecimal.ZERO);
		order.setOpenFee(BigDecimal.ZERO);
		order.setCloseFee(BigDecimal.ZERO);
		
		this.pcOrderDAO.update(order);
		
		return;
	}

	public void setNewStatus(Long userId, Long orderId, Integer newStatus, Integer desiredOldStatus){
		long count = this.pcOrderDAO.changeStatus(userId, orderId, newStatus, desiredOldStatus, new Date());
		if(count!=1){
//			throw new RuntimeException("更新失败，更新行数："+count);
		}
	}

	/*
	 * 检查可平仓位
	 */
	private void checkClosablePosition(PcPosition pos, BigDecimal number) {
		if(pos==null){
			throw new ExException(PositonError.POS_NOT_ENOUGH);
		}
		if(pos.getLiqStatus()==LiqStatus.YES){
			throw new ExException(PositonError.POS_NOT_ENOUGH);
		}
		
		BigDecimal closablePos = pos.getVolume();
		
		BigDecimal cpv = this.pcOrderDAO.getClosingVolume(pos.getUserId(), pos.getId());
		if(cpv!=null){
			closablePos = closablePos.subtract(cpv);
		}
		
        //判断可平仓位是否足够
        if (BigUtils.gt(number, closablePos)) {
            throw new ExException(PositonError.POS_NOT_ENOUGH);
        }
	}
	
	@CrossDB
	public List<PcOrder> pageQuery(Page page, Integer status, Date modified){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		params.put("status", status);
		params.put("modifiedEnd", modified);
		List<PcOrder> list = this.pcOrderDAO.queryList(params);
		return list;
	}
	
}
