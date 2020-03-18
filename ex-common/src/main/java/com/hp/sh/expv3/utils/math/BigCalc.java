package com.hp.sh.expv3.utils.math;

import java.math.BigDecimal;

/**
 * 计算
 * @author wangjg
 *
 */
public class BigCalc {

	public static BigDecimal multiply(BigDecimal v1, BigDecimal v2){
		BigDecimal result =  v2.multiply(v1);
		return result.stripTrailingZeros();
	}
	
	/**
	 * 求和
	 * @param values
	 * @return
	 */
	public static BigDecimal sum(BigDecimal...values){
		BigDecimal result = BigDecimal.ZERO;
		for(BigDecimal value : values){
			result = result.add(value);
		}
		return result;
	}

	
	/**
	 * 求和
	 * @param values
	 * @return
	 */
	public static BigDecimal subtract(BigDecimal n, BigDecimal...values){
		BigDecimal result = n;
		for(BigDecimal value : values){
			result = result.subtract(value);
		}
		return result;
	}
	
	/**
	 * 按比例计算amount
	 * @param number 比例分子
	 * @param total 比例分母
	 * @param amount 求值对象
	 * @return
	 */
	public static BigDecimal slope(BigDecimal number, BigDecimal total, BigDecimal amount){
		return number.multiply(amount).divide(total, Precision.COMMON_PRECISION, Precision.LESS).stripTrailingZeros();
	}
	
}
