package com.hp.sh.expv3.bb.component;

/**
 * 选择收费员
 * @author wangjg
 *
 */
public interface FeeCollectorSelector {
	
	public Long getFeeCollectorId(Long userId, String asset, String symbol);

}
