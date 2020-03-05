
package com.hp.sh.expv3.bb.module.order.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.mapper.BaseAccountDataMapper;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;

/**
 * 
 * @author wangjg
 *
 */
public interface BBOrderDAO extends BaseAccountDataMapper<BBOrder, Long> {

	public List<BBOrder> queryList(Map<String, Object> params);

	public BBOrder queryOne(Map<String, Object> params);

	public Long queryCount(Map<String, Object> params);
	
	int update(BBOrder entity);

	public long updateStatus(
		@Param("newStatus") Integer newStatus,
		@Param("modified") Long modified,
		@Param("orderId") Long orderId,
		@Param("userId") Long userId, 
		@Param("version") Long version
	);

	public List<BBOrder> queryActiveOrderList(@Param("userId") Long userId, @Param("asset") String asset, @Param("symbol") String symbol);

}
