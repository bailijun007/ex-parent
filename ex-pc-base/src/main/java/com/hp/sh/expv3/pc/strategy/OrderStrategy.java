package com.hp.sh.expv3.pc.strategy;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.strategy.data.MarginParam;
import com.hp.sh.expv3.pc.strategy.data.OrderFeeParam;
import com.hp.sh.expv3.pc.strategy.vo.OrderFeeData;
import com.hp.sh.expv3.utils.math.BigCalc;
import com.hp.sh.expv3.utils.math.BigUtils;

/**
 * 订单计算策略
 * @author wangjg
 *
 */
public interface OrderStrategy {
	
	BigDecimal calcAmount(BigDecimal volume, BigDecimal faceValue, BigDecimal price);
	
	BigDecimal calcBaseValue(BigDecimal volume, BigDecimal faceValue, BigDecimal price);

	OrderFeeData calcNewOrderFee(OrderFeeParam pcOrder);

	BigDecimal calMargin(BigDecimal volume, BigDecimal faceValue, BigDecimal price, BigDecimal marginRatio);

	BigDecimal calcTradeFee(BigDecimal volume, BigDecimal faceValue, BigDecimal price, BigDecimal feeRatio);

	/**
	 * 按比例计算开仓订单费用
	 * @param order
	 * @param number
	 * @return
	 */
	default public OrderFeeData calcRaitoFee(MarginParam order, BigDecimal total, BigDecimal number){
		OrderFeeData orderAmount = new OrderFeeData();
		
		BigDecimal openFee; 
		BigDecimal closeFee; 
		BigDecimal orderMargin;
	
		if(BigUtils.eq(number, BigDecimal.ZERO)){
			openFee = BigDecimal.ZERO;
			closeFee = BigDecimal.ZERO;
			orderMargin = BigDecimal.ZERO;
		}else if(BigUtils.eq(number, total)){
			openFee = order.getOpenFee();
			closeFee = order.getCloseFee();
			orderMargin = order.getOrderMargin();
		}else{
			openFee = BigCalc.slope(number, total, order.getOpenFee());
			closeFee = BigCalc.slope(number, total, order.getCloseFee());
			orderMargin = BigCalc.slope(number, total, order.getOrderMargin());
		}
		
		orderAmount.setOrderMargin(orderMargin);
		orderAmount.setOpenFee(openFee);
		orderAmount.setCloseFee(closeFee);
		orderAmount.setOrderMargin(orderMargin);
		orderAmount.setGrossMargin(BigCalc.sum(openFee, closeFee, orderMargin));
		
		return orderAmount;
	}
	
}
