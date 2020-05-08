
package com.hp.sh.expv3.bb.module.order.dao;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.base.mapper.BaseMapper;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderHistory;

/**
 * 
 * @author wangjg
 *
 */
public interface BBOrderHistoryDAO extends BaseMapper<BBOrderHistory,Long> {

	public List<BBOrderHistory> queryList(Map<String,Object> params);

}
