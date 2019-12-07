package com.hp.sh.expv3.pc.component;

import org.springframework.stereotype.Component;

@Component
public class FeeCollectorSelector {
	
	public Long getFeeCollectorId(Long userId, String asset, String symbol){
		return 1L;
	}

}
