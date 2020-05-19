
package com.hp.sh.expv3.pc.module.order.dao;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.base.mapper.BaseMapper;
import com.hp.sh.expv3.pc.module.order.entity.PcOrderHistory;

/**
 * 
 * @author wangjg
 *
 */
public interface PcOrderHistoryDAO extends BaseMapper<PcOrderHistory,Long> {

	public List<PcOrderHistory> queryList(Map<String,Object> params);

}
