
package com.hp.sh.expv3.bb.module.position.dao;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.bb.module.position.entity.BBLiqRecord;

/**
 * 
 * @author wangjg
 *
 */
public interface BBLiqRecordDAO extends BaseUserDataMapper<BBLiqRecord,Long> {

	public List<BBLiqRecord> queryList(Map<String,Object> params);
	
	public BBLiqRecord queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

}
