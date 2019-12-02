
package com.hp.sh.expv3.fund.cash.dao;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.fund.cash.entity.DepositAddr;

/**
 * 
 * @author wangjg
 *
 */
public interface DepositAddrDAO extends BaseUserDataMapper<DepositAddr,Long> {

	public List<DepositAddr> queryList(Map<String,Object> params);
	
	public DepositAddr queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

}
