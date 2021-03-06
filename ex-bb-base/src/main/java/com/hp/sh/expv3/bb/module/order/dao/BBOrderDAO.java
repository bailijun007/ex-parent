
package com.hp.sh.expv3.bb.module.order.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.base.mapper.BaseMapper;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.dev.CrossDB;

/**
 * 
 * @author wangjg
 *
 */
public interface BBOrderDAO extends BaseMapper<BBOrder, Long> {

	public BBOrder findById(@Param("asset") String asset, @Param("symbol") String symbol, @Param("userId") Long userId, @Param("id") Long id);

	public BBOrder lockById(@Param("asset") String asset, @Param("symbol") String symbol, @Param("userId") Long userId, @Param("id") Long id);

	public List<BBOrder> queryList(Map<String, Object> params);

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
	
	@CrossDB
	public List<BBOrder> queryPendingActiveOrders(Page page, @Param("symbol") String symbol, @Param("createdEnd") long createdEnd, @Param("status") int status, @Param("startId") Long startId);

	public void delete(@Param("id") Long id, @Param("userId") Long userId, @Param("asset") String asset, @Param("symbol") String symbol);
	
	public long exist(@Param("userId") long userId, @Param("asset") String asset, @Param("symbol") String symbol, @Param("bidFlag") Integer bidFlag);
	
}
