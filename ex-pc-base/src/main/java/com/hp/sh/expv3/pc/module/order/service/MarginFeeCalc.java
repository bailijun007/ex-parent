package com.hp.sh.expv3.pc.module.order.service;

import java.math.BigDecimal;

public class MarginFeeCalc {
	

	public static final BigDecimal calMargin(BigDecimal value, BigDecimal ratio){
		return ratio.multiply(value);
	
	}

	public static final BigDecimal calcFee(BigDecimal value, BigDecimal ratio){
		return ratio.multiply(value);
		
	}
	
}
