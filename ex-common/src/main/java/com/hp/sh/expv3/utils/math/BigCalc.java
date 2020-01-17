package com.hp.sh.expv3.utils.math;

import java.math.BigDecimal;

/**
 * 计算
 * @author wangjg
 *
 */
public class BigCalc {

	
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
