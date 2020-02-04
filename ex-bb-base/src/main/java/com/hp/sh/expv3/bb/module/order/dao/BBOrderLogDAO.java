
package com.hp.sh.expv3.bb.module.order.dao;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderLog;

/**
 * 
 * @author wangjg
 *
 */
public interface BBOrderLogDAO extends BaseUserDataMapper<BBOrderLog,Long> {

	public List<BBOrderLog> queryList(Map<String,Object> params);
	
	public BBOrderLog queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

}
