
package com.hp.sh.expv3.bb.module.fail.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.entity.BaseBizEntity;
import com.hp.sh.expv3.base.entity.UserDataEntity;
import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.bb.module.fail.entity.BBMqMsg;

/**
 * 
 * @author wangjg
 *
 */
public interface BBMqMsgDAO extends BaseUserDataMapper<BBMqMsg,Long> {

	public List<BBMqMsg> queryList(Map<String,Object> params);
	
	public BBMqMsg queryOne(Map<String,Object> params);
	
	public BBMqMsg queryTrade(Map<String,Object> params);
	
	public List<BBMqMsg> queryTradeList(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

	public void delete(@Param(UserDataEntity.USERID_PROPERTY) Long userId, @Param(BaseBizEntity.ID_PROPERTY) Long id);

}
