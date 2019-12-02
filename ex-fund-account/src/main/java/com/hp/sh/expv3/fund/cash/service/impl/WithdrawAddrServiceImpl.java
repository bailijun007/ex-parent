
package com.hp.sh.expv3.fund.cash.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.base.entity.UserDataEntity;
import com.hp.sh.expv3.fund.cash.dao.WithdrawalAddrDAO;
import com.hp.sh.expv3.fund.cash.entity.WithdrawalAddr;
import com.hp.sh.expv3.fund.cash.service.WithdrawAddrService;

/**
 * 
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class WithdrawAddrServiceImpl implements WithdrawAddrService{

	@Autowired
	private WithdrawalAddrDAO withdrawalAddrDAO;

	@Override
	public void save(WithdrawalAddr withdrawalAddr){
		Date now = new Date();
		if(withdrawalAddr.getId()==null){
			withdrawalAddr.setCreated(now);
			withdrawalAddr.setModified(now);
			this.withdrawalAddrDAO.save(withdrawalAddr);
		}else{
			withdrawalAddr.setModified(now);
			this.withdrawalAddrDAO.update(withdrawalAddr);
		}
	}

	@Override
	public void batchSave(List<WithdrawalAddr> list){
		Date now = new Date();
		for(WithdrawalAddr withdrawalAddr:list){
			if(withdrawalAddr.getId()==null){
				withdrawalAddr.setCreated(now);
				withdrawalAddr.setModified(now);
				this.withdrawalAddrDAO.save(withdrawalAddr);
			}else{
			withdrawalAddr.setModified(now);
			this.withdrawalAddrDAO.update(withdrawalAddr);
			}
		}
	}

	@Override
	public void update(WithdrawalAddr withdrawalAddr){
		Date now = new Date();
		withdrawalAddr.setModified(now);
		this.withdrawalAddrDAO.update(withdrawalAddr);
	}

	@Override
	public WithdrawalAddr findWithdrawAddr(long accountId, String asset) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(UserDataEntity.USERID_PROPERTY, accountId);
		WithdrawalAddr addr = this.withdrawalAddrDAO.queryOne(params);
		return addr;
	}

	@Override
	public void delete(Long userId, Long id) {
		WithdrawalAddr withdrawalAddr = this.withdrawalAddrDAO.findById(userId, id);
		withdrawalAddr.setEnabled(WithdrawalAddr.NO);
		this.withdrawalAddrDAO.update(withdrawalAddr);
	}

}
