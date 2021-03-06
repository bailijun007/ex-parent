
package com.hp.sh.expv3.fund.cash.service;

import com.hp.sh.expv3.fund.cash.entity.DepositAddr;

/**
 * 
 * @author wangjg
 *
 */
public interface DepositAddrService {

	public void save(DepositAddr depositAddr);

	public void update(DepositAddr depositAddr);

	public DepositAddr findById(long userId, long depositAddrId);

	public DepositAddr getDepositAddress(Long userId, String asset);

}
