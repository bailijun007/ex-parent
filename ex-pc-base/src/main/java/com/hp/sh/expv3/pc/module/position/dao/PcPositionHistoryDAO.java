
package com.hp.sh.expv3.pc.module.position.dao;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.base.mapper.BaseMapper;
import com.hp.sh.expv3.pc.module.position.entity.PcPositionHistory;

/**
 * 
 * @author wangjg
 *
 */
public interface PcPositionHistoryDAO extends BaseMapper<PcPositionHistory,Long> {

	public List<PcPositionHistory> queryList(Map<String,Object> params);

}
