package com.hp.sh.expv3.utils.math;

import java.math.BigDecimal;

/**
 * 
 * @author wangjg
 *
 */
public class BigUtils {

	//是否负数
	public static boolean ltZero(BigDecimal val){
		return val.compareTo(BigDecimal.ZERO)<0;
	}

	//是否大于零
	public static boolean gtZero(BigDecimal val){
		return val.compareTo(BigDecimal.ZERO)>0;
	}
	
	//是否正数
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
	
}