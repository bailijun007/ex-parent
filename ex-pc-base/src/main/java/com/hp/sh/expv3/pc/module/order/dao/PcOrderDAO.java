
package com.hp.sh.expv3.pc.module.order.dao;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.base.mapper.BaseAccountDataMapper;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;

/**
 * 
 * @author wangjg
 *
 */
public interface PcOrderDAO extends BaseAccountDataMapper<PcOrder,Long> {

	public List<PcOrder> queryList(Map<String,Object> params);
	
	public PcOrder queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

}
