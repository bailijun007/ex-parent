
package com.hp.sh.expv3.pc.module.account.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.entity.BaseBizEntity;
import com.hp.sh.expv3.base.entity.UserDataEntity;
import com.hp.sh.expv3.base.mapper.BaseAccountDataMapper;
import com.hp.sh.expv3.pc.module.account.entity.PcAccountRecord;

/**
 * 
 * @author wangjg
 *
 */
public interface PcAccountRecordDAO extends BaseAccountDataMapper<PcAccountRecord,Long> {

	public List<PcAccountRecord> queryList(Map<String,Object> params);
	
	public PcAccountRecord queryOne(Map<String,Object> params);

	public Double queryCount(Map<String,Object> params);

	@Deprecated
	public PcAccountRecord findByTradeNo(@Param("userId") Long userId, @Param("tradeNo") String tradeNo);
	
	public PcAccountRecord findById(@Param("asset") String asset, @Param(UserDataEntity.USERID_PROPERTY) Long userId, @Param(BaseBizEntity.ID_PROPERTY) Long id);

}
