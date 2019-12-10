package com.hp.sh.expv3.pc.module.symbol.service;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.module.symbol.entity.PcAccountSymbol;

/**
 * 
 * @author wangjg
 *
 */
public interface PcAccountSymbolService {

	void create(Long userId, String asset, String symbol);

	PcAccountSymbol get(Long userId, String asset, String symbol);

	//TODO 锁
	BigDecimal getLeverage(long userId, String asset, String symbol, int longFlag);

}
