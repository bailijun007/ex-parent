
package com.hp.sh.expv3.bb.module.order.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderTrade;

/**
 * 
 * @author wangjg
 *
 */
public interface BBOrderTradeDAO extends BaseUserDataMapper<BBOrderTrade,String> {

	public List<BBOrderTrade> queryList(Map<String,Object> params);
	
	public BBOrderTrade queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

	public Long exist(@Param("userId") Long userId, @Param("tradeSn") String tradeSn);

	public void setSynchStatus(@Param("asset") String asset, @Param("symbol") String symbol, @Param("userId") Long userId, @Param("id") Long id, @Param("feeSynchStatus") Integer feeSynchStatus, @Param("modified") Long modified);

}
