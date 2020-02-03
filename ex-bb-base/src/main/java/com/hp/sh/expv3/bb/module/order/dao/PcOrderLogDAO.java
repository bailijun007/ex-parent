
package com.hp.sh.expv3.bb.module.order.dao;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.bb.module.order.entity.PcOrderLog;

/**
 * 
 * @author wangjg
 *
 */
public interface PcOrderLogDAO extends BaseUserDataMapper<PcOrderLog,Long> {

	public List<PcOrderLog> queryList(Map<String,Object> params);
	
	public PcOrderLog queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

}
