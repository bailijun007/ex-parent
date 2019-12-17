
package com.hp.sh.expv3.pc.module.order.dao;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.pc.module.order.entity.PcOrderTrade;

/**
 * 
 * @author wangjg
 *
 */
public interface PcOrderTradeDAO extends BaseUserDataMapper<PcOrderTrade,Long> {

	public List<PcOrderTrade> queryList(Map<String,Object> params);
	
	public PcOrderTrade queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

}