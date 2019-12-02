
package com.hp.sh.expv3.fund.cash.service;

import java.util.List;

import com.hp.sh.expv3.fund.cash.entity.WithdrawalAddr;

/**
 * 
 * @author wangjg
 *
 */
public interface WithdrawAddrService {

	public void save(WithdrawalAddr withdrawalAddr);

	public void batchSave(List<WithdrawalAddr> list);

	public void update(WithdrawalAddr withdrawalAddr);

	public void delete(Long userId, Long id);

	public WithdrawalAddr findWithdrawAddr(long accountId, String asset);

}
