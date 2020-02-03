
package com.hp.sh.expv3.bb.module.position.dao;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.bb.module.position.entity.PcLiqRecord;

/**
 * 
 * @author wangjg
 *
 */
public interface PcLiqRecordDAO extends BaseUserDataMapper<PcLiqRecord,Long> {

	public List<PcLiqRecord> queryList(Map<String,Object> params);
	
	public PcLiqRecord queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

}
