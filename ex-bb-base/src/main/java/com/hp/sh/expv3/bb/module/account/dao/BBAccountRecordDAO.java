
package com.hp.sh.expv3.bb.module.account.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

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

	public BBAccountRecord findByTradeNo(@Param("userId") Long userId, @Param("tradeNo") String tradeNo);

}
