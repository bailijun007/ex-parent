
package com.hp.sh.expv3.bb.module.collector.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.mapper.BaseAccountDataMapper;
import com.hp.sh.expv3.bb.module.collector.entity.BBCollectorAccountRecord;

/**
 * 
 * @author wangjg
 *
 */
public interface BBCollectorAccountRecordDAO extends BaseAccountDataMapper<BBCollectorAccountRecord,Long> {

	public List<BBCollectorAccountRecord> queryList(Map<String,Object> params);
	
	public BBCollectorAccountRecord queryOne(Map<String,Object> params);

	public Double queryCount(Map<String,Object> params);

	public BBCollectorAccountRecord findByTradeNo(@Param("userId") Long userId, @Param("tradeNo") String tradeNo);

}