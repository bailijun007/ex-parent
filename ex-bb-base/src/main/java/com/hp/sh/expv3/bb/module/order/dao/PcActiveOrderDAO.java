
package com.hp.sh.expv3.bb.module.order.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.bb.module.order.entity.PcActiveOrder;

/**
 * 
 * @author wangjg
 *
 */
public interface PcActiveOrderDAO extends BaseUserDataMapper<PcActiveOrder,Long> {

	public List<PcActiveOrder> queryList(Map<String,Object> params);
	
	public PcActiveOrder queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

	public void delete(@Param("id") Long id, @Param("userId") Long userId);
	
	public List<Long> queryActiveOrderIds(@Param("userId") Long userId, @Param("asset") String asset, @Param("symbol") String symbol);

	public long exist(@Param("userId") long userId, @Param("asset") String asset, @Param("symbol") String symbol, @Param("longFlag") Integer longFlag);

}
