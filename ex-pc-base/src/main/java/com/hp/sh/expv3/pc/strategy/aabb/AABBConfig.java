package com.hp.sh.expv3.pc.strategy.aabb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hp.sh.expv3.pc.constant.PcContractType;
import com.hp.sh.expv3.pc.strategy.StrategyBundle;

@Configuration
public class AABBConfig {
	
	@Bean
	public StrategyBundle aabbStrategyBundle(){
		return new StrategyBundle(PcContractType.REVERSE, new AABBHoldPosStrategy(), new AABBOrderStrategy());
	}

}
