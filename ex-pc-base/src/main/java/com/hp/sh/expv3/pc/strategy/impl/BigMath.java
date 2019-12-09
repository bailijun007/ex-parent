package com.hp.sh.expv3.pc.strategy.impl;

import java.math.BigDecimal;

public class BigMath {
	
	public static boolean isZero(BigDecimal v1){
		return v1.compareTo(BigDecimal.ZERO)==0;
	}

	public static boolean eq(BigDecimal v1, BigDecimal v2){
		return v1.compareTo(v2)==0;
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
