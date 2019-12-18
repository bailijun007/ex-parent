package com.hp.sh.expv3.pc.component.mock;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.component.FeeCollectorSelector;

/**
 * 选择收费员
 * @author wangjg
 *
 */
@Primary
@Component
public class FeeCollectorSelectorImpl implements FeeCollectorSelector{
	
	public Long getFeeCollectorId(Long userId, String asset, String symbol){
		return 1L;
	}

}
