
package com.hp.sh.expv3.pc.module.riskfund.dao;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.pc.module.riskfund.entity.PcRiskfundAccount;

/**
 * 
 * @author wangjg
 *
 */
public interface PcRiskfundAccountDAO {
	
	public PcRiskfundAccount get(@Param("collectorId") Long collectorId, @Param("asset") String asset);
	
	void save(PcRiskfundAccount entity);

	int update(PcRiskfundAccount entity);

}
