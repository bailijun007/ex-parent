
package com.hp.sh.expv3.bb.module.account.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.entity.BaseBizEntity;
import com.hp.sh.expv3.base.entity.UserDataEntity;
import com.hp.sh.expv3.base.mapper.BaseAccountDataMapper;
import com.hp.sh.expv3.bb.module.account.entity.BBAccountRecord;

/**
 * 
 * @author wangjg
 *
 */
public interface BBAccountRecordDAO extends BaseAccountDataMapper<BBAccountRecord,Long> {

	public List<BBAccountRecord> queryList(Map<String,Object> params);
	
	public BBAccountRecord queryOne(Map<String,Object> params);

	public Double queryCount(Map<String,Object> params);

	@Deprecated
	public BBAccountRecord findByTradeNo(@Param("asset") String asset, @Param("userId") Long userId, @Param("tradeNo") String tradeNo);

	public BBAccountRecord findById(@Param("asset") String asset, @Param(UserDataEntity.USERID_PROPERTY) Long userId, @Param(BaseBizEntity.ID_PROPERTY) Long id);

}
