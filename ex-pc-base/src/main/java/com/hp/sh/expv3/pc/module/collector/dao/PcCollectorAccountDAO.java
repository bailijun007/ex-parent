
package com.hp.sh.expv3.pc.module.collector.dao;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.pc.module.collector.entity.PcCollectorAccount;

/**
 * 
 * @author wangjg
 *
 */
public interface PcCollectorAccountDAO {
	
	public PcCollectorAccount get(@Param("collectorId") Long collectorId, @Param("asset") String asset);
	
	public PcCollectorAccount getAndLock(@Param("collectorId") Long collectorId, @Param("asset") String asset);
	
	void save(PcCollectorAccount entity);

	int update(PcCollectorAccount entity);

}
