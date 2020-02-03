package com.hp.sh.expv3.bb.strategy.common;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.calc.CompFieldCalc;
import com.hp.sh.expv3.bb.calc.OrderFeeCalc;
import com.hp.sh.expv3.bb.constant.OrderFlag;
import com.hp.sh.expv3.bb.module.order.entity.PcOrder;
import com.hp.sh.expv3.bb.strategy.OrderStrategy;
import com.hp.sh.expv3.bb.strategy.data.OrderFeeParam;
import com.hp.sh.expv3.bb.strategy.vo.OrderRatioData;
import com.hp.sh.expv3.utils.math.BigCalc;
import com.hp.sh.expv3.utils.math.BigUtils;
import com.hp.sh.expv3.utils.math.Precision;

/**
 * 
 * @author wangjg
 *
 */
@Component
public class CommonOrderStrategy implements OrderStrategy {
	
	/**
	 * 计算新订单费用
	 */
		//交易金额
	public OrderRatioData calcOrderAmt(OrderFeeParam pcOrder){
		BigDecimal amount = CompFieldCalc.calcAmount(pcOrder.getVolume(), pcOrder.getFaceValue());
		//基础货币价值
		BigDecimal baseValue = CompFieldCalc.calcBaseValue(amount, pcOrder.getPrice());
		
		//开仓手续费
		BigDecimal openFee = this.calcFee(baseValue, pcOrder.getOpenFeeRatio());
		
		//平仓手续费
		BigDecimal closeFee = this.calcFee(baseValue, pcOrder.getCloseFeeRatio());
		
		//保证金
		BigDecimal orderMargin = OrderFeeCalc.calMargin(baseValue, pcOrder.getMarginRatio());
		
		//总押金
		BigDecimal grossMargin = BigCalc.sum(closeFee, openFee, orderMargin);

		OrderRatioData orderAmount = new OrderRatioData();
		orderAmount.setAmount(amount);
		orderAmount.setBaseValue(baseValue);
		
		orderAmount.setOrderMargin(orderMargin);
		orderAmount.setOpenFee(openFee);
		orderAmount.setCloseFee(closeFee);
		orderAmount.setOrderMargin(orderMargin);
		orderAmount.setGrossMargin(grossMargin);
		
		return orderAmount;
	}
	
	public BigDecimal calMargin(BigDecimal volume, BigDecimal faceValue, BigDecimal price, BigDecimal marginRatio){
		BigDecimal amount = CompFieldCalc.calcAmount(volume, faceValue);
		//基础货币价值
		BigDecimal baseValue = CompFieldCalc.calcBaseValue(amount, price);
		
		//保证金
		BigDecimal orderMargin = OrderFeeCalc.calMargin(baseValue, marginRatio);
		return orderMargin;
	}
	
	public BigDecimal calcFee(BigDecimal baseValue, BigDecimal feeRatio){
		BigDecimal fee = OrderFeeCalc.calcFee(baseValue, feeRatio);
		return fee.stripTrailingZeros();
	}
	
	/**
	 * 按比例计算费用
	 * @param order 订单数据
	 * @param volume 分量
	 * @return
	 */
	public OrderRatioData calcRaitoAmt(PcOrder order, BigDecimal number){
		OrderRatioData orderAmount = new OrderRatioData();
		if(order.getCloseFlag() == OrderFlag.ACTION_OPEN){
			this.calcOpenRaitoAmt(order, number, orderAmount);
		}else{
			this.calcCloseRaitoAmt(order, number, orderAmount);
		}
		return orderAmount;
	}
	
	protected void calcCloseRaitoAmt(PcOrder order, BigDecimal number, OrderRatioData orderAmount) {
		orderAmount.setOpenFee(BigDecimal.ZERO);
		orderAmount.setCloseFee(BigDecimal.ZERO);
		orderAmount.setOrderMargin(BigDecimal.ZERO);
		orderAmount.setGrossMargin(BigDecimal.ZERO);
		
		BigDecimal amount = number.multiply(order.getFaceValue());
		BigDecimal baseValue = CompFieldCalc.calcBaseValue(amount, order.getPrice());

		orderAmount.setAmount(amount);
		orderAmount.setBaseValue(baseValue);
	}

	/**
	 * 按比例计算开仓订单费用
	 * @param order
	 * @param number
	 * @return
	 */
	protected void calcOpenRaitoAmt(PcOrder order, BigDecimal number, OrderRatioData orderAmount){
		
		BigDecimal openFee; 
		BigDecimal closeFee; 
		BigDecimal orderMargin;
		BigDecimal grossMargin;

		if(BigUtils.eq(number, BigDecimal.ZERO)){
			openFee = BigDecimal.ZERO;
			closeFee = BigDecimal.ZERO;
			orderMargin = BigDecimal.ZERO;
			grossMargin = BigDecimal.ZERO;
		}else if(BigUtils.eq(number, order.getVolume().subtract(order.getFilledVolume()))){
			openFee = order.getOpenFee();
			closeFee = order.getCloseFee();
			orderMargin = order.getOrderMargin();
			grossMargin = order.getGrossMargin();
		}else{
			openFee = slope(number, order.getVolume(), order.getOpenFee());
			closeFee = slope(number, order.getVolume(), order.getCloseFee());
			orderMargin = slope(number, order.getVolume(), order.getOrderMargin());
			grossMargin = BigCalc.sum(openFee, closeFee, orderMargin);
		}
		
		orderAmount.setOrderMargin(orderMargin);
		orderAmount.setOpenFee(openFee);
		orderAmount.setCloseFee(closeFee);
		orderAmount.setOrderMargin(orderMargin);
		orderAmount.setGrossMargin(grossMargin);

		
		BigDecimal amount = number.multiply(order.getFaceValue());
		BigDecimal baseValue = CompFieldCalc.calcBaseValue(amount, order.getPrice());

		orderAmount.setAmount(amount);
		orderAmount.setBaseValue(baseValue);
	}
	
	/**
	 * 按比例计算amount
	 * @param number 比例分子
	 * @param volume 比例分母
	 * @param amount 求值对象
	 * @return
	 */
	public static final BigDecimal slope(BigDecimal number, BigDecimal volume, BigDecimal amount){
		return number.multiply(amount).divide(volume, Precision.COMMON_PRECISION, Precision.LESS).stripTrailingZeros();
	}
	
}
