package com.hp.sh.expv3.pc.module.order.service;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.module.account.api.request.CutMoneyRequest;
import com.hp.sh.expv3.pc.module.account.service.impl.PcAccountCoreService;
import com.hp.sh.expv3.pc.module.order.constant.MarginMode;
import com.hp.sh.expv3.pc.module.order.constant.PcAccountTradeType;
import com.hp.sh.expv3.pc.module.order.constant.PcOrderType;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderDAO;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.symbol.entity.PcAccountSymbol;
import com.hp.sh.expv3.pc.module.symbol.service.PcAccountSymbolService;
import com.hp.sh.expv3.utils.IntBool;

/**
 * 委托
 * @author lw
 *
 */
@Service
@Transactional
public class PcOrderService {
	
	@Autowired
	private PcAccountSymbolService pcAccountSymbolService;

	@Autowired
	private PcOrderDAO pcOrderDAO;
	
	@Autowired
	private MarginRatioService marginRatioService;
	
	@Autowired
	private PcAccountCoreService pcAccountCoreService;
	
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
	public PcOrder create(long userId, String cliOrderId, String asset, String symbol, int closeFlag, int longFlag, int timeInForce, BigDecimal price, BigDecimal amt){
		
		Date now = new Date();
		PcOrder pcOrder = new PcOrder();
		
		pcOrder.setAccountId(userId);
		
		pcOrder.setUserId(userId);
		pcOrder.setCreated(now);
		pcOrder.setModified(now);
		
		pcOrder.setAsset(asset);
		pcOrder.setSymbol(symbol);
		pcOrder.setCloseFlag(closeFlag);
		pcOrder.setLongFlag(longFlag);
		pcOrder.setLeverage(this.getLeverage(userId, asset, symbol, IntBool.isTrue(longFlag)));
		pcOrder.setPrice(price);
		pcOrder.setAmt(amt);
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
		this.cutBalance(userId, asset, pcOrder.getId().toString(), pcOrder.getGrossMargin());
		
		return pcOrder;
	}
	
	private void setOther(PcOrder pcOrder){
		pcOrder.setFeeCost(BigDecimal.ZERO);
		pcOrder.setFilledAmt(BigDecimal.ZERO);
		pcOrder.setFilledVolume(BigDecimal.ZERO);
		pcOrder.setCancelAmt(null);
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
		pcOrder.setCancelAmt(BigDecimal.ZERO);
	}
	
	private void cutBalance(Long userId, String asset, String tradeNo, BigDecimal amount){
		CutMoneyRequest request = new CutMoneyRequest();
		request.setAmount(amount);
		request.setAsset(asset);
		request.setRemark("开仓扣除");
		request.setTradeNo(tradeNo);
		request.setTradeType(PcAccountTradeType.ORDER_OPEN);
		request.setUserId(userId);
		this.pcAccountCoreService.cut(request);
	}

	//设置平仓订单的各种费率
	private void setCloseOrderFee(PcOrder pcOrder) {
        //判断可平仓位是否足够
        if (false) {
            throw new ExException(null);
        }

        if (null == null) {
            
        }
        
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
		pcOrder.setMarginRatio(marginRatioService.getInitedMarginRatio(pcOrder.getLeverage()));
		pcOrder.setOpenFeeRatio(marginRatioService.getOpenFeeRatio(pcOrder.getUserId()));
		pcOrder.setCloseFeeRatio(marginRatioService.getCloseFeeRatio(pcOrder.getUserId()));
		
		//交易量
		BigDecimal volume = MarginFeeCalc.calcVolume(pcOrder.getAmt(), pcOrder.getPrice());
		
		pcOrder.setOrderMargin(MarginFeeCalc.calcMargin(IntBool.isTrue(pcOrder.getCloseFlag()), pcOrder.getMarginRatio(), volume));
		pcOrder.setOpenFee(MarginFeeCalc.calcOpenFee(pcOrder.getOpenFeeRatio(), volume));
		pcOrder.setCloseFee(MarginFeeCalc.calcCloseFee(pcOrder.getCloseFeeRatio(), volume));
		pcOrder.setGrossMargin(pcOrder.getOrderMargin().add(pcOrder.getOpenFee()).add(pcOrder.getCloseFee()));
	}

	private BigDecimal getLeverage(long userId, String asset, String symbol, boolean isLong){
		PcAccountSymbol as = pcAccountSymbolService.get(userId, asset, symbol);
		if(isLong){	//多
			return as.getLongLeverage();
		}else{		//空
			return as.getShortLeverage();
		}
	}
	
	public void cancel(long userId, String asset, String symbol, long orderId){
		Date now = new Date();
		
		PcOrder order = this.pcOrderDAO.findById(userId, orderId);
		
		order.setStatus(PcOrder.PENDING_CANCEL);
		order.setActiveFlag(PcOrder.NO);
		order.setCancelTime(now);
		order.setModified(now);
        
	}
	
	public void changeStatus(Long userId, Long orderId, Integer newStatus, Integer oldStatus){
		this.pcOrderDAO.changeStatus(userId, orderId, newStatus, oldStatus, new Date());
	}

}
