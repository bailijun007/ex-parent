
package com.hp.sh.expv3.bb.module.collector.dao;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.bb.module.collector.entity.BBCollectorAccount;

/**
 * 
 * @author wangjg
 *
 */
public interface BBCollectorAccountDAO {
	
	public BBCollectorAccount get(@Param("userId") Long userId, @Param("asset") String asset);
	
	public BBCollectorAccount getAndLock(@Param("userId") Long userId, @Param("asset") String asset);
	
	void save(BBCollectorAccount entity);

	int update(BBCollectorAccount entity);

}
