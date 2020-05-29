
package com.hp.sh.expv3.pc.module.order.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.base.entity.BaseBizEntity;
import com.hp.sh.expv3.base.entity.UserDataEntity;
import com.hp.sh.expv3.base.mapper.BaseAccountDataMapper;
import com.hp.sh.expv3.dev.CrossDB;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;

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
		@Param("newStatus") Integer newStatus,
		@Param("modified") Long modified,
		@Param("orderId") Long orderId,
		@Param("userId") Long userId, 
		@Param("version") Long version
	);

	public BigDecimal getClosingVolume(@Param("userId") Long userId, @Param("asset") String asset, @Param("symbol") String symbol, @Param("posId") Long posId);
	
	public List<PcOrder> queryUserActiveOrderList(Page page, @Param("userId") Long userId, @Param("asset") String asset, @Param("symbol") String symbol, @Param("status") Integer status, @Param("liqFlag") Integer liqFlag, @Param("startId") Long startId);
	
	//按id正序
	@CrossDB
	public List<PcOrder> queryActiveOrderList(Page page, @Param("asset") String asset, @Param("symbol") String symbol, @Param("createdEnd") Long createdEnd, @Param("status") Integer status, @Param("liqFlag") Integer liqFlag, @Param("startId") Long startId);

	public PcOrder lockById(@Param(UserDataEntity.USERID_PROPERTY) Long userId, @Param(BaseBizEntity.ID_PROPERTY) Long id);
	
	public long exist(@Param("userId") long userId, @Param("asset") String asset, @Param("symbol") String symbol, @Param("longFlag") Integer longFlag);

	public void delete(@Param("id") long id, @Param("userId") long userId, @Param("asset") String asset, @Param("symbol") String symbol);

}
