
package com.hp.sh.expv3.bb.module.order.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.mapper.BaseAccountDataMapper;
import com.hp.sh.expv3.bb.module.order.entity.PcOrder;

/**
 * 
 * @author wangjg
 *
 */
public interface PcOrderDAO extends BaseAccountDataMapper<PcOrder, Long> {

	public List<PcOrder> queryList(Map<String, Object> params);

	public PcOrder queryOne(Map<String, Object> params);

	public Long queryCount(Map<String, Object> params);
	
	int update(PcOrder entity);

	public long updateStatus(
		@Param("orderId") Long orderId,
		@Param("userId") Long userId, 
		@Param("newStatus") Integer newStatus, 
		@Param("oldStatus") Integer oldStatus,
		@Param("modified") Long modified
	);

	public long updateCancelStatus(
		@Param("orderId") Long orderId,
		@Param("userId") Long userId, 
		@Param("cancelStatus") Integer cancelStatus, 
		@Param("modified") Long modified,
		@Param("status1") Integer status1,
		@Param("status2") Integer status2,
		@Param("activeFlag") Integer activeFlag
	);

	public BigDecimal getClosingVolume(@Param("userId") Long userId, @Param("asset") String asset, @Param("symbol") String symbol, @Param("posId") Long posId);
	
	public List<PcOrder> queryActiveOrderList(@Param("userId") Long userId, @Param("asset") String asset, @Param("symbol") String symbol);

}
