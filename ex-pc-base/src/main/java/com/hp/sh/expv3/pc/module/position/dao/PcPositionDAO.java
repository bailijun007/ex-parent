
package com.hp.sh.expv3.pc.module.position.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.position.vo.PosUID;

/**
 * 
 * @author wangjg
 *
 */
public interface PcPositionDAO extends BaseUserDataMapper<PcPosition,Long> {

	@Override
    public int update(PcPosition entity);

	public List<PcPosition> queryList(Map<String,Object> params);
	
	public PcPosition queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);
	
	public BigDecimal queryAmount(Map<String,Object> params);
	
	public PcPosition getActivePos(
			@Param("userId") long userId, 
			@Param("asset") String asset,
			@Param("symbol") String symbol, 
			@Param("longFlag") int longFlag);

	@Deprecated
	public List<PcPosition> queryActivePosList(
			Page page,
			@Param("userId") Long userId, 
			@Param("asset") String asset,
			@Param("symbol") String symbol);
	
	public List<PosUID> queryActivePosIdList(
			Page page,
			@Param("userId") Long userId, 
			@Param("asset") String asset,
			@Param("symbol") String symbol,
			@Param("startId") Long startId);

	public PcPosition getCutPos(
		@Param("asset") String asset,
		@Param("symbol") String symbol,
		@Param("longFlag") Integer longFlag
	);

	public void delete(@Param("id") long id, @Param("userId") long userId, @Param("asset") String asset, @Param("symbol") String symbol);

}
