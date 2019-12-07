package com.hp.sh.expv3.pc.component;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

/**
 * 面值查询
 * @author wangjg
 *
 */
@Component
public class FaceValueQuery {
	
	public BigDecimal get(String symbol){
		
		return BigDecimal.ONE;
	}

}
