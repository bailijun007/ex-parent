
package com.hp.sh.expv3.pc.module.msg.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.pc.module.msg.entity.PcMessageExt;

/**
 * 
 * @author wangjg
 *
 */
public interface PcMessageExtDAO extends BaseUserDataMapper<PcMessageExt,Long> {

	public List<PcMessageExt> queryList(Map<String,Object> params);
	
	public PcMessageExt queryOne(Map<String,Object> params);
	
	public PcMessageExt queryTrade(Map<String,Object> params);
	
	public List<PcMessageExt> queryTradeList(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

	public void delete(@Param("userId") Long userId, @Param("id") Long id);

	public void setStatus(@Param("userId") Long userId, @Param("id") Long id, @Param("status") Integer status, @Param("errorInfo") String errorInfo);

}
