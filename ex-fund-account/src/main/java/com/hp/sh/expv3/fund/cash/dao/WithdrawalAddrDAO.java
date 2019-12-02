
package com.hp.sh.expv3.fund.cash.dao;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.fund.cash.entity.WithdrawalAddr;

/**
 * 
 * @author wangjg
 *
 */
public interface WithdrawalAddrDAO extends BaseUserDataMapper<WithdrawalAddr,Long> {

	public List<WithdrawalAddr> queryList(Map<String,Object> params);
	
	public WithdrawalAddr queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

}
