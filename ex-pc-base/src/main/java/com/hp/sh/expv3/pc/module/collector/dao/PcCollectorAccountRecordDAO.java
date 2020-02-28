
package com.hp.sh.expv3.pc.module.collector.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.mapper.BaseAccountDataMapper;
import com.hp.sh.expv3.pc.module.collector.entity.PcCollectorAccountRecord;

/**
 * 
 * @author wangjg
 *
 */
public interface PcCollectorAccountRecordDAO extends BaseAccountDataMapper<PcCollectorAccountRecord,Long> {

	public List<PcCollectorAccountRecord> queryList(Map<String,Object> params);
	
	public PcCollectorAccountRecord queryOne(Map<String,Object> params);

	public PcCollectorAccountRecord findByTradeNo(@Param("collectorId") Long collectorId, @Param("tradeNo") String tradeNo);

}
