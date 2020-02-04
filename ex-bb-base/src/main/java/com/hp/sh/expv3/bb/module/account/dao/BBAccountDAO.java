
package com.hp.sh.expv3.bb.module.account.dao;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.bb.module.account.entity.BBAccount;

/**
 * 
 * @author wangjg
 *
 */
public interface BBAccountDAO {
	
	public BBAccount get(@Param("userId") Long userId, @Param("asset") String asset);
	
	public BBAccount getAndLock(@Param("userId") Long userId, @Param("asset") String asset);
	
	void save(BBAccount entity);

	int update(BBAccount entity);

}
