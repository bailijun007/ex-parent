package com.hp.sh.expv3.utils.math;

import java.math.BigDecimal;

/**
 * BigCompare
 * @author wangjg
 *
 */
public class BigUtils {
	
	//是否小于0
	public static boolean ltZero(BigDecimal val){
		return val.compareTo(BigDecimal.ZERO)<0;
	}
	
	//是否小于等于0
	public static boolean leZero(BigDecimal val){
		return val.compareTo(BigDecimal.ZERO)<=0;
	}

	//大于零
	public static boolean gtZero(BigDecimal val){
		return val.compareTo(BigDecimal.ZERO)>0;
	}
	
	//大于等于零
	public static boolean geZero(BigDecimal val){
		return val.compareTo(BigDecimal.ZERO)>=0;
	}

	public static boolean isZero(BigDecimal v1){
		return v1.compareTo(BigDecimal.ZERO)==0;
	}

	public static boolean eq(BigDecimal v1, BigDecimal v2){
		return v1.compareTo(v2)==0;
	}

	public static boolean ne(BigDecimal v1, BigDecimal v2){
		return v1.compareTo(v2)!=0;
	}
	
	public static boolean gt(BigDecimal v1, BigDecimal v2){
		return v1.compareTo(v2)>0;
	}
	
	public static boolean ge(BigDecimal v1, BigDecimal v2){
		return v1.compareTo(v2)>=0;
	}

	public static boolean lt(BigDecimal v1, BigDecimal v2){
		return v1.compareTo(v2)<0;
	}

	public static boolean le(BigDecimal v1, BigDecimal v2){
		return v1.compareTo(v2)<=0;
	}

	public static boolean between(BigDecimal v, BigDecimal start, BigDecimal end){
		return ge(v, start) && le(v, end);
	}

	public static boolean haveDecimalPart(BigDecimal num){
		BigDecimal rem = num.divideAndRemainder(BigDecimal.ONE)[1];
		return gtZero(rem);
	}

	public static BigDecimal getDecimalPart(BigDecimal num){
		return num.divideAndRemainder(BigDecimal.ONE)[1];
	}
	
	public static void main(String[] args) {
		System.out.println(getDecimalPart(new BigDecimal("123.456")));
	}
	
}
