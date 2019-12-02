
package com.hp.sh.expv3.fund.wallet.dao;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.fund.wallet.entity.FundAccount;

/**
 * 
 * @author wangjg
 *
 */
public interface FundAccountDAO {
	
	public FundAccount get(@Param("userId") Long userId, @Param("asset") String asset);
	
	public FundAccount getAndLock(@Param("userId") Long userId, @Param("asset") String asset);
	
	void save(FundAccount entity);

	int update(FundAccount entity);

}
