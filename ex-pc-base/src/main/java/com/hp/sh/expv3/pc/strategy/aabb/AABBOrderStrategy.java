package com.hp.sh.expv3.pc.strategy.aabb;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.strategy.OrderStrategy;
import com.hp.sh.expv3.pc.strategy.data.OrderFeeParam;
import com.hp.sh.expv3.pc.strategy.vo.OrderFeeData;
import com.hp.sh.expv3.utils.math.BigCalc;

/**
 * 
 * @author wangjg
 *
 */
public class AABBOrderStrategy implements OrderStrategy {
	
	public BigDecimal calcTradeFee(BigDecimal volume, BigDecimal faceValue, BigDecimal price, BigDecimal feeRatio) {
		BigDecimal result = BigCalc.multiply(this.calcBaseValue(volume, faceValue, price), feeRatio);
		return result;
	}

	/**
	 * 计算交易合约 基础货币总价值
	 * @param volume 合约张数
	 * @param faceValue 面值
	 * @param price 成交价格
	 * @return
	 */
	public BigDecimal calcAmount(BigDecimal volume, BigDecimal faceValue, BigDecimal price){
		return AABBCompFieldCalc.calcAmount(volume, faceValue);
	}
	
	/**
	 * 计算交易合约 基础货币总价值
	 * @param volume 合约张数
	 * @param faceValue 面值
	 * @param price 成交价格
	 * @return
	 */
	public BigDecimal calcBaseValue(BigDecimal volume, BigDecimal faceValue, BigDecimal price){
		return AABBCompFieldCalc.calcBaseValue(volume, faceValue, price);
	}
	
	/**
	 * 计算新订单费用
	 */
	@Override
	public OrderFeeData calcNewOrderFee(OrderFeeParam pcOrder){
		//交易金额
		BigDecimal _amount = AABBCompFieldCalc.calcAmount(pcOrder.getVolume(), pcOrder.getFaceValue());
		
		BigDecimal baseValue = AABBCompFieldCalc.calcBaseValue(_amount, pcOrder.getPrice());
		
		//开仓手续费
		BigDecimal openFee = BigCalc.multiply(baseValue, pcOrder.getOpenFeeRatio());
		
		//平仓手续费
		BigDecimal closeFee = BigCalc.multiply(baseValue, pcOrder.getCloseFeeRatio());
		
		//保证金
		BigDecimal orderMargin = this.calMargin(baseValue, pcOrder.getMarginRatio());
		
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

	public final BigDecimal calMargin(BigDecimal volume, BigDecimal faceValue, BigDecimal price, BigDecimal marginRatio){
		BigDecimal amount = AABBCompFieldCalc.calcAmount(volume, faceValue);
		//基础货币价值
		BigDecimal baseValue = AABBCompFieldCalc.calcBaseValue(amount, price);
		
		//保证金
		BigDecimal orderMargin = this.calMargin(baseValue, marginRatio);
		return orderMargin;
	}

	/**
	 * 计算保证金
	 * @param baseValue 基础货币价值
	 * @param ratio
	 * @return
	 */
	private final BigDecimal calMargin(BigDecimal baseValue, BigDecimal ratio){
		return ratio.multiply(baseValue);
	}

}
