
package com.hp.sh.expv3.bb.module.position.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.bb.module.position.entity.BBPosition;
import com.hp.sh.expv3.bb.module.position.vo.PosUID;

/**
 * 
 * @author wangjg
 *
 */
public interface BBPositionDAO extends BaseUserDataMapper<BBPosition,Long> {

	@Override
    public int update(BBPosition entity);

	public List<BBPosition> queryList(Map<String,Object> params);
	
	public BBPosition queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);
	
	public BigDecimal queryAmount(Map<String,Object> params);
	
	public BBPosition getActivePos(
			@Param("userId") long userId, 
			@Param("asset") String asset,
			@Param("symbol") String symbol, 
			@Param("longFlag") int longFlag);

	@Deprecated
	public List<BBPosition> queryActivePosList(
			Page page,
			@Param("userId") Long userId, 
			@Param("asset") String asset,
			@Param("symbol") String symbol);
	
	public List<PosUID> queryActivePosIdList(
			Page page,
			@Param("userId") Long userId, 
			@Param("asset") String asset,
			@Param("symbol") String symbol);

}
