
package com.hp.sh.expv3.pc.module.order.dao;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.base.mapper.BaseMapper;
import com.hp.sh.expv3.pc.module.order.entity.PcAccountLog;

/**
 * 
 * @author wangjg
 *
 */
public interface PcAccountLogDAO extends BaseMapper<PcAccountLog,Long> {

	public List<PcAccountLog> queryList(Map<String,Object> params);
	
	public PcAccountLog queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

}
