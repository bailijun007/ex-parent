package com.hp.sh.expv3.pc.strategy.aaab;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hp.sh.expv3.pc.constant.PcContractType;
import com.hp.sh.expv3.pc.strategy.StrategyBundle;

@Configuration
public class AAABConfig {
	
	@Bean
	public StrategyBundle aaabStrategyBundle(){
		return new StrategyBundle(PcContractType.FORWARD, new AAABHoldPosStrategy(), new AAABOrderStrategy());
	}

}
