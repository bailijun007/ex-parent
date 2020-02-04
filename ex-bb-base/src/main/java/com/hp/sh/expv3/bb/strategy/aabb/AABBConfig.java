package com.hp.sh.expv3.bb.strategy.aabb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hp.sh.expv3.bb.constant.BBContractType;
import com.hp.sh.expv3.bb.strategy.StrategyBundle;

@Configuration
public class AABBConfig {
	
	@Bean
	public AABBHoldPosStrategy aabbHoldPosStrategy(){
		return new AABBHoldPosStrategy();
	}
	
	@Bean
	public StrategyBundle aabbStrategyBundle(AABBHoldPosStrategy aabbHoldPosStrategy){
		return new StrategyBundle(BBContractType.REVERSE, aabbHoldPosStrategy);
	}

}
