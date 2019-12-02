package com.hp.sh.expv3.pc.module.symbol.service;

import com.hp.sh.expv3.pc.module.symbol.entity.PcAccountSymbol;

/**
 * 
 * @author wangjg
 *
 */
public interface PcAccountSymbolService {

	void create(Long userId, String asset, String symbol);

	PcAccountSymbol get(Long userId, String asset, String symbol);

}
