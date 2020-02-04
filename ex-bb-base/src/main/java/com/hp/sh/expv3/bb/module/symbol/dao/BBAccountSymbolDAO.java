
package com.hp.sh.expv3.bb.module.symbol.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.bb.module.symbol.entity.BBAccountSymbol;

/**
 * 
 * @author wangjg
 *
 */
public interface BBAccountSymbolDAO extends BaseUserDataMapper<BBAccountSymbol,Long> {

	public List<BBAccountSymbol> queryList(Map<String,Object> params);
	
	public BBAccountSymbol queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

	public BBAccountSymbol getUserSymbol(@Param("userId") Long userId, @Param("asset") String asset, @Param("symbol") String symbol);
	
	public BBAccountSymbol lockUserSymbol(@Param("userId") Long userId, @Param("asset") String asset, @Param("symbol") String symbol);

}
