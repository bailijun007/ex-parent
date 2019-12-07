
package com.hp.sh.expv3.pc.module.symbol.dao;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.pc.module.symbol.entity.PcAccountSymbol;

/**
 * 
 * @author wangjg
 *
 */
public interface PcAccountSymbolDAO extends BaseUserDataMapper<PcAccountSymbol,Long> {

	public List<PcAccountSymbol> queryList(Map<String,Object> params);
	
	public PcAccountSymbol queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

	public PcAccountSymbol getUserSymbol(Long userId, String asset, String symbol);
	
	public PcAccountSymbol lockUserSymbol(Long userId, String asset, String symbol);

}
