package com.hp.sh.expv3.pc.module.order.service;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.constant.Precision;

public class BaseValueCalc {

	public static final BigDecimal calcValue(BigDecimal amount, BigDecimal price){
		return amount.divide(price, Precision.COMMON_PRECISION, Precision.LESS);
			}	
}
