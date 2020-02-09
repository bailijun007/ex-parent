
package com.hp.sh.expv3.bb.module.account.dao;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.base.mapper.BaseMapper;
import com.hp.sh.expv3.bb.msg.BBAccountLog;

/**
 * 
 * @author wangjg
 *
 */
public interface BBAccountLogDAO extends BaseMapper<BBAccountLog,Long> {

	public List<BBAccountLog> queryList(Map<String,Object> params);
	
	public BBAccountLog queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

}
