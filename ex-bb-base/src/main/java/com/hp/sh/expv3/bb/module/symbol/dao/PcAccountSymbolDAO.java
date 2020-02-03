
package com.hp.sh.expv3.bb.module.symbol.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.bb.module.symbol.entity.PcAccountSymbol;

/**
 * 
 * @author wangjg
 *
 */
public interface PcAccountSymbolDAO extends BaseUserDataMapper<PcAccountSymbol,Long> {

	public List<PcAccountSymbol> queryList(Map<String,Object> params);
	
	public PcAccountSymbol queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

	public PcAccountSymbol getUserSymbol(@Param("userId") Long userId, @Param("asset") String asset, @Param("symbol") String symbol);
	
	public PcAccountSymbol lockUserSymbol(@Param("userId") Long userId, @Param("asset") String asset, @Param("symbol") String symbol);

}
