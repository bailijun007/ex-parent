
package com.hp.sh.expv3.bb.module.msg.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.entity.BaseBizEntity;
import com.hp.sh.expv3.base.entity.UserDataEntity;
import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.bb.module.msg.entity.BBMessageExt;

/**
 * 
 * @author wangjg
 *
 */
public interface BBMessageExtDAO extends BaseUserDataMapper<BBMessageExt,String> {

	public List<BBMessageExt> queryList(Map<String,Object> params);
	
	public BBMessageExt queryOne(Map<String,Object> params);
	
	public BBMessageExt queryTrade(Map<String,Object> params);
	
	public List<BBMessageExt> queryTradeList(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

	public void delete(@Param("userId") Long userId, @Param("msgId") String msgId);

	public void setStatus(@Param("userId") Long userId, @Param("msgId") String msgId, @Param("status") Integer status, @Param("errorInfo") String errorInfo);

}
