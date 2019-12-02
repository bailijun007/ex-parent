
package com.hp.sh.expv3.fund.cash.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.fund.cash.dao.DepositAddrDAO;
import com.hp.sh.expv3.fund.cash.entity.DepositAddr;
import com.hp.sh.expv3.fund.cash.service.DepositAddrService;

/**
 * 
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class DepositAddrServiceImpl implements DepositAddrService{

	@Autowired
	private DepositAddrDAO depositAddrDAO;

	@Override
	public void save(DepositAddr depositAddr){
		Date now = new Date();
		if(depositAddr.getId()==null){
			depositAddr.setCreated(now);
			depositAddr.setModified(now);
			this.depositAddrDAO.save(depositAddr);
		}else{
			depositAddr.setModified(now);
			this.depositAddrDAO.update(depositAddr);
		}
	}

	@Override
	public void update(DepositAddr depositAddr){
		Date now = new Date();
		depositAddr.setModified(now);
		this.depositAddrDAO.update(depositAddr);
	}

}
