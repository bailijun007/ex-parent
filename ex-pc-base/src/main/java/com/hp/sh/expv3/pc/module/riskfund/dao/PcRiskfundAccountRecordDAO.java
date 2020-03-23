
package com.hp.sh.expv3.pc.module.riskfund.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.mapper.BaseMapper;
import com.hp.sh.expv3.pc.module.riskfund.entity.PcRiskfundAccountRecord;

/**
 * 
 * @author wangjg
 *
 */
public interface PcRiskfundAccountRecordDAO extends BaseMapper<PcRiskfundAccountRecord,Long> {

	public List<PcRiskfundAccountRecord> queryList(Map<String,Object> params);
	
	public PcRiskfundAccountRecord queryOne(Map<String,Object> params);

	public PcRiskfundAccountRecord findByTradeNo(@Param("id") Long collectorId, @Param("tradeNo") String tradeNo);

}
