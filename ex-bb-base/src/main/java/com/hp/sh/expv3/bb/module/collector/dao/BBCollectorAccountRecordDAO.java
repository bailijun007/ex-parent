
package com.hp.sh.expv3.bb.module.collector.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.mapper.BaseMapper;
import com.hp.sh.expv3.bb.module.collector.entity.BBCollectorAccountRecord;

/**
 * 
 * @author wangjg
 *
 */
public interface BBCollectorAccountRecordDAO extends BaseMapper<BBCollectorAccountRecord,Long> {

	public List<BBCollectorAccountRecord> queryList(Map<String,Object> params);
	
	public BBCollectorAccountRecord queryOne(Map<String,Object> params);

	public BBCollectorAccountRecord findByTradeNo(@Param("id") Long collectorId, @Param("tradeNo") String tradeNo);

}
