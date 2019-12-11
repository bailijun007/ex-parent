package com.hp.sh.expv3.pc.calc;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.constant.Precision;

/**
 * 订单费用计算
 * @author wangjg
 *
 */
public class MarginFeeCalc {
	

	/**
	 * 计算保证金
	 * @param value 
	 * @param ratio
	 * @return
	 */
	public static final BigDecimal calMargin(BigDecimal value, BigDecimal ratio){
		return ratio.multiply(value);
	}

	public static final BigDecimal calcFee(BigDecimal value, BigDecimal ratio){
		return ratio.multiply(value);
	}

	/**
	 * 计算比例
	 * @param number 除数
	 * @param total 总数
	 * @return
	 */
	public static final BigDecimal calcRatio(BigDecimal number, BigDecimal total){
		return number.divide(total, Precision.COMMON_PRECISION, Precision.LESS).stripTrailingZeros();
	}

	/**
	 * 求和
	 * @param values
	 * @return
	 */
	public static final BigDecimal sum(BigDecimal...values){
		BigDecimal result = BigDecimal.ZERO;
		for(BigDecimal value : values){
			result = result.add(value);
		}
		return result;
	}
	
}
