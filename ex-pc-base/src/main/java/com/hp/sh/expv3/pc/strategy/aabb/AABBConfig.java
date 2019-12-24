package com.hp.sh.expv3.pc.strategy.aabb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hp.sh.expv3.pc.constant.PcContractType;
import com.hp.sh.expv3.pc.strategy.StrategyBundle;

@Configuration
public class AABBConfig {
	
	@Bean
	public AABBHoldPosStrategy aabbHoldPosStrategy(){
		return new AABBHoldPosStrategy();
	}
	
	@Bean
	public StrategyBundle aabbStrategyBundle(AABBHoldPosStrategy aabbHoldPosStrategy){
		return new StrategyBundle(PcContractType.REVERSE, aabbHoldPosStrategy);
	}

}
