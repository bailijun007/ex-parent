
package com.hp.sh.expv3.pc.module.position.dao;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.base.mapper.BaseMapper;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;

/**
 * 
 * @author wangjg
 *
 */
public interface PcPositionDAO extends BaseMapper<PcPosition,Long> {

	public List<PcPosition> queryList(Map<String,Object> params);
	
	public PcPosition queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

}
