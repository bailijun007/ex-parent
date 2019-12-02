
package com.hp.sh.expv3.pc.module.account.dao;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.pc.module.account.entity.PcAccount;

/**
 * 
 * @author wangjg
 *
 */
public interface PcAccountDAO {
	
	public PcAccount get(@Param("userId") Long userId, @Param("asset") String asset);
	
	public PcAccount getAndLock(@Param("userId") Long userId, @Param("asset") String asset);
	
	void save(PcAccount entity);

	int update(PcAccount entity);

}
