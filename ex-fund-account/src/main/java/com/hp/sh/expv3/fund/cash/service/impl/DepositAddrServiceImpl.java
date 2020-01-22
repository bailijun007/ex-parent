
package com.hp.sh.expv3.fund.cash.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.fund.cash.dao.DepositAddrDAO;
import com.hp.sh.expv3.fund.cash.entity.DepositAddr;
import com.hp.sh.expv3.fund.cash.service.DepositAddrService;
import com.hp.sh.expv3.utils.DbDateUtils;

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
		Long now = DbDateUtils.now();
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
		Long now = DbDateUtils.now();
		depositAddr.setModified(now);
		this.depositAddrDAO.update(depositAddr);
	}

	@Override
	public DepositAddr findById(long userId, long id) {
		return this.depositAddrDAO.findById(userId, id);
	}

	@Override
	public DepositAddr getDepositAddress(Long userId, String asset) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("asset", asset);
		DepositAddr addr = this.depositAddrDAO.queryOne(params);
		return addr;
	}

}
