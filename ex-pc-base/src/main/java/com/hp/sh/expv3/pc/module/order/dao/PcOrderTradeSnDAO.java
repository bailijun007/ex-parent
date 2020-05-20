
package com.hp.sh.expv3.pc.module.order.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.mapper.BaseMapper;
import com.hp.sh.expv3.pc.module.order.entity.PcOrderTradeSn;

/**
 * 
 * @author wangjg
 *
 */
public interface PcOrderTradeSnDAO extends BaseMapper<PcOrderTradeSn, String>{

	public List<PcOrderTradeSn> queryList(Map<String,Object> params);
	
	public PcOrderTradeSn queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

	public Long exist(@Param("tradeSn") String tradeSn);

}
