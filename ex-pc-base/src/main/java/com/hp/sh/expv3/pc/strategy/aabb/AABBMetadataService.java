package com.hp.sh.expv3.pc.strategy.aabb;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

/**
 * 面值查询
 * @author wangjg
 *
 */
@Component
public class AABBMetadataService {
	
	public BigDecimal getFaceValue(String symbol){
		
		return BigDecimal.ONE;
	}

}