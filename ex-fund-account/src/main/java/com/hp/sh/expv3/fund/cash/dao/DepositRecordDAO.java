
package com.hp.sh.expv3.fund.cash.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.entity.UserDataEntity;
import com.hp.sh.expv3.base.mapper.BaseAccountDataMapper;
import com.hp.sh.expv3.fund.cash.entity.DepositRecord;

/**
 * 
 * @author wangjg
 *
 */
public interface DepositRecordDAO extends BaseAccountDataMapper<DepositRecord,Long> {

	public List<DepositRecord> queryList(Map<String,Object> params);
	
	public DepositRecord queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

	public DepositRecord findBySn(@Param(UserDataEntity.USERID_PROPERTY) Long userId, @Param("sn") String sn);

}