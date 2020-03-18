
package com.hp.sh.expv3.bb.module.collector.dao;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.bb.module.collector.entity.BBCollectorAccount;

/**
 * 
 * @author wangjg
 *
 */
public interface BBCollectorAccountDAO {
	
	public BBCollectorAccount get(@Param("id") Long collectorId, @Param("asset") String asset);
	
	void save(BBCollectorAccount entity);

	int update(BBCollectorAccount entity);

}
