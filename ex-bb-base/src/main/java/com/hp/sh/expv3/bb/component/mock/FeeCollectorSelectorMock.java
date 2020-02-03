package com.hp.sh.expv3.bb.component.mock;

import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.component.FeeCollectorSelector;

/**
 * 选择收费员
 * @author wangjg
 *
 */
@Component
public class FeeCollectorSelectorMock implements FeeCollectorSelector{
	
	public Long getFeeCollectorId(Long userId, String asset, String symbol){
		return 1L;
	}

}
