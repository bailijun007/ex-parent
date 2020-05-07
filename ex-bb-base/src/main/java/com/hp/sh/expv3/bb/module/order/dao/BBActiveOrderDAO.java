
package com.hp.sh.expv3.bb.module.order.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.bb.module.order.entity.BBActiveOrder;

/**
 * 
 * @author wangjg
 *
 */
public interface BBActiveOrderDAO extends BaseUserDataMapper<BBActiveOrder,Long> {

	public List<BBActiveOrder> queryList(Map<String,Object> params);
	
	public BBActiveOrder queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

	public void delete(@Param("id") Long id, @Param("userId") Long userId);
	
	public long exist(@Param("userId") long userId, @Param("asset") String asset, @Param("symbol") String symbol, @Param("bidFlag") Integer bidFlag);

}
