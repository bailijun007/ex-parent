package com.hp.sh.expv3.pc.strategy.aaab;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.strategy.OrderStrategy;
import com.hp.sh.expv3.pc.strategy.data.OrderFeeParam;
import com.hp.sh.expv3.pc.strategy.vo.OrderFeeData;
import com.hp.sh.expv3.utils.math.BigCalc;
import com.hp.sh.expv3.utils.math.BigUtils;

/**
 * 
 * @author wangjg
 *
 */
public class AAABOrderStrategy implements OrderStrategy {
	
	public BigDecimal calcTradeFee(BigDecimal volume, BigDecimal faceValue, BigDecimal price, BigDecimal feeRatio) {
		return BigCalc.multiply(volume.multiply(faceValue).multiply(price), feeRatio);
	}

	/**
	 * 计算交易合约 基础货币总价值
	 * @param volume 合约张数
	 * @param faceValue 面值
	 * @param price 成交价格
	 * @return
	 */
	public BigDecimal calcAmount(BigDecimal volume, BigDecimal faceValue, BigDecimal price){
		BigDecimal fv = faceValue.multiply(volume);
		BigDecimal amount = fv.multiply(price);
		return amount;
	}
	
	public BigDecimal calcBaseValue(BigDecimal volume, BigDecimal faceValue, BigDecimal price){
		return volume.multiply(faceValue);
	}
	
	/**
	 * 计算新订单费用
	 */
	public OrderFeeData calcNewOrderFee(OrderFeeParam order){
		//交易金额
		BigDecimal _amount = this.calcAmount(order.getVolume(), order.getFaceValue(), order.getPrice());
		
		//开仓手续费
		BigDecimal openFee = BigCalc.multiply(_amount, order.getOpenFeeRatio());
		
		//平仓手续费
		BigDecimal closeFee = BigCalc.multiply(_amount, order.getCloseFeeRatio());
		
		//保证金
		BigDecimal orderMargin = this.calMargin(_amount, order.getMarginRatio());
		
		//总押金
		BigDecimal grossMargin = BigCalc.sum(closeFee, openFee, orderMargin);

		OrderFeeData orderAmount = new OrderFeeData();
		
		orderAmount.setOrderMargin(orderMargin);
		orderAmount.setOpenFee(openFee);
		orderAmount.setCloseFee(closeFee);
		orderAmount.setOrderMargin(orderMargin);
		orderAmount.setGrossMargin(grossMargin);
		
		return orderAmount;
	}
	
	/**
	 * 按比例计算费用
	 * @param order 订单数据
	 * @param volume 分量
	 * @return
	 */
	public OrderFeeData calcRaitoOrderFee(PcOrder order, BigDecimal number){
		OrderFeeData orderAmount = new OrderFeeData();
		if(order.getCloseFlag() == OrderFlag.ACTION_OPEN){
			this.calcOpenRaitoAmt(order, number, orderAmount);
		}else{
			this.calcCloseRaitoAmt(order, number, orderAmount);
		}
		return orderAmount;
	}
	
	protected void calcCloseRaitoAmt(PcOrder order, BigDecimal number, OrderFeeData orderAmount) {
		orderAmount.setOpenFee(BigDecimal.ZERO);
		orderAmount.setCloseFee(BigDecimal.ZERO);
		orderAmount.setOrderMargin(BigDecimal.ZERO);
		orderAmount.setGrossMargin(BigDecimal.ZERO);
	}

	/**
	 * 按比例计算开仓订单费用
	 * @param order
	 * @param number
	 * @return
	 */
	protected void calcOpenRaitoAmt(PcOrder order, BigDecimal number, OrderFeeData orderAmount){
		
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
			openFee = BigCalc.slope(number, order.getVolume(), order.getOpenFee());
			closeFee = BigCalc.slope(number, order.getVolume(), order.getCloseFee());
			orderMargin = BigCalc.slope(number, order.getVolume(), order.getOrderMargin());
			grossMargin = BigCalc.sum(openFee, closeFee, orderMargin);
		}
		
		orderAmount.setOrderMargin(orderMargin);
		orderAmount.setOpenFee(openFee);
		orderAmount.setCloseFee(closeFee);
		orderAmount.setOrderMargin(orderMargin);
		orderAmount.setGrossMargin(grossMargin);

	}

	public final BigDecimal calMargin(BigDecimal volume, BigDecimal faceValue, BigDecimal price, BigDecimal marginRatio){
		BigDecimal amount = this.calcAmount(volume, faceValue, price);
		//保证金
		BigDecimal orderMargin = this.calMargin(amount, marginRatio);
		return orderMargin;
	}

	/**
	 * 计算保证金
	 * @param amount
	 * @param ratio
	 * @return
	 */
	private final BigDecimal calMargin(BigDecimal amount, BigDecimal ratio){
		return ratio.multiply(amount);
	}
	
}
