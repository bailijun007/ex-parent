
package com.hp.sh.expv3.bb.module.order.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.mapper.BaseMapper;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderTradeSn;

/**
 * 
 * @author wangjg
 *
 */
public interface BBOrderTradeSnDAO extends BaseMapper<BBOrderTradeSn, String>{

	public List<BBOrderTradeSn> queryList(Map<String,Object> params);
	
	public BBOrderTradeSn queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

	public Long exist(@Param("tradeSn") String tradeSn);

}
