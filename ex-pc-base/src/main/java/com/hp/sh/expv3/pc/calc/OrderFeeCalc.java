package com.hp.sh.expv3.pc.calc;

import java.math.BigDecimal;

/**
 * 订单费用计算
 * @author wangjg
 *
 */
public class OrderFeeCalc {
	
	/**
	 * 计算保证金
	 * @param baseValue 基础货币价值
	 * @param ratio
	 * @return
	 */
	public static final BigDecimal calMargin(BigDecimal baseValue, BigDecimal ratio){
		return ratio.multiply(baseValue);
	}

	/**
	 * 计算收续费
	 * @param baseValue
	 * @param ratio
	 * @return
	 */
	public static final BigDecimal calcFee(BigDecimal baseValue, BigDecimal ratio){
		return ratio.multiply(baseValue);
	}

	/**
	 * 计算总押金
	 * @param openFee
	 * @param closeFee
	 * @param orderMargin
	 * @return
	 */
	public static final BigDecimal calcGrossMargin(BigDecimal openFee, BigDecimal closeFee, BigDecimal orderMargin){
		BigDecimal grossMargin = OrderFeeCalc.sum(openFee, closeFee, orderMargin);
		return grossMargin;
	}
	
	/**
	 * 求和
	 * @param values
	 * @return
	 */
	private static final BigDecimal sum(BigDecimal...values){
		BigDecimal result = BigDecimal.ZERO;
		for(BigDecimal value : values){
			result = result.add(value);
		}
		return result;
	}
	
}
