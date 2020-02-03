package com.hp.sh.expv3.bb.calc;

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
	
}
