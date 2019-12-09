package com.hp.sh.expv3.pc.strategy.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.calc.CompositeFieldCalc;
import com.hp.sh.expv3.pc.calc.FeeCalc;
import com.hp.sh.expv3.pc.calc.MarginFeeCalc;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.strategy.OrderStrategy;
import com.hp.sh.expv3.pc.strategy.vo.OrderBasicData2;
import com.hp.sh.expv3.pc.strategy.vo.OrderFee;

/**
 * 
 * @author wangjg
 *
 */
@Component
public class AABBOrderStrategy implements OrderStrategy {
	
	/**
	 * 计算新订单费用
	 * @param pcOrder
	 * @return
	 */
	public OrderFee calcFee(PcOrder pcOrder){
		//交易金额
		BigDecimal amount = CompositeFieldCalc.calcAmount(pcOrder.getVolume(), pcOrder.getFaceValue());
		//基础货币价值
		BigDecimal baseValue = CompositeFieldCalc.calcBaseValue(amount, pcOrder.getPrice());
		
		//开仓手续费
		BigDecimal openFee = MarginFeeCalc.calcFee(baseValue, pcOrder.getOpenFeeRatio());
		
		//平仓手续费
		BigDecimal closeFee = MarginFeeCalc.calcFee(baseValue, pcOrder.getCloseFeeRatio());
		
		//保证金
		BigDecimal orderMargin = MarginFeeCalc.calMargin(baseValue, pcOrder.getMarginRatio());
		
		//总押金
		BigDecimal grossMargin = MarginFeeCalc.sum(closeFee, openFee, orderMargin);

		OrderFee orderFee = new OrderFee();
		orderFee.setOrderMargin(orderMargin);
		orderFee.setOpenFee(openFee);
		orderFee.setCloseFee(closeFee);
		orderFee.setOrderMargin(orderMargin);
		orderFee.setGrossMargin(grossMargin);
		
		return orderFee;
	}
	
	/**
	 * 按比例计算费用
	 * @param order 订单数据
	 * @param volume 分量
	 * @return
	 */
	public OrderFee calcRaitoFee(PcOrder order, BigDecimal number){
		
		BigDecimal openFee; 
		BigDecimal closeFee; 
		BigDecimal orderMargin;
		BigDecimal grossMargin;
		if(BigMath.eq(number, BigDecimal.ZERO)){
			openFee = BigDecimal.ZERO;
			closeFee = BigDecimal.ZERO;
			orderMargin = BigDecimal.ZERO;
			grossMargin = BigDecimal.ZERO;
		}else if(BigMath.eq(number, order.getVolume())){
			openFee = order.getOpenFee();
			closeFee = order.getCloseFee();
			orderMargin = order.getOrderMargin();
			grossMargin = order.getGrossMargin();
		}else{
			openFee = slope(number, order.getVolume(), order.getOpenFee());
			closeFee = slope(number, order.getVolume(), order.getCloseFee());
			orderMargin = slope(number, order.getVolume(), order.getOrderMargin());
			grossMargin = slope(number, order.getVolume(), order.getGrossMargin());
		}
		
		OrderFee orderFee = new OrderFee();
		orderFee.setOrderMargin(orderMargin);
		orderFee.setOpenFee(openFee);
		orderFee.setCloseFee(closeFee);
		orderFee.setOrderMargin(orderMargin);
		orderFee.setGrossMargin(grossMargin);
		
		return orderFee;
	}
	
	/**
	 * 按比例计算amount
	 * @param number 比例分子
	 * @param volume 比例分母
	 * @param amount 求值分母
	 * @return
	 */
	private BigDecimal slope(BigDecimal number, BigDecimal volume, BigDecimal amount){
		return FeeCalc.slope(volume, number, amount);
	}
	
}
