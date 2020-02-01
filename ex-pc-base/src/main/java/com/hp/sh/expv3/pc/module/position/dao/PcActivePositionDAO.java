
package com.hp.sh.expv3.pc.module.position.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.mapper.BaseMapper;
import com.hp.sh.expv3.pc.module.position.entity.PcActivePosition;

/**
 * 
 * @author wangjg
 *
 */
public interface PcActivePositionDAO extends BaseMapper<PcActivePosition,Long> {

	public List<PcActivePosition> queryList(Map<String,Object> params);
	
	public PcActivePosition queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

	public void delete(@Param("id") Long id, @Param("userId") Long userId);
	
	public List<Long> queryActiveOrderIds(@Param("userId") Long userId, @Param("asset") String asset, @Param("symbol") String symbol);

	public long exist(@Param("userId") long userId, @Param("asset") String asset, @Param("symbol") String symbol, @Param("longFlag") Integer longFlag);

}
