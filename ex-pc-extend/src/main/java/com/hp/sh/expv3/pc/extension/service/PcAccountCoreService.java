/**
 * 
 */
package com.hp.sh.expv3.pc.extension.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.pc.extension.dao.PcAccountDAO;
import com.hp.sh.expv3.pc.module.account.entity.PcAccount;

/**
 * @author wangjg
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class PcAccountCoreService{
	private static final Logger logger = LoggerFactory.getLogger(PcAccountCoreService.class);

	@Autowired
	private PcAccountDAO pcAccountDAO;

	public BigDecimal getBalance(Long userId, String asset){
		PcAccount fa = this.pcAccountDAO.get(userId, asset);
		if(fa==null){
			return null;
		}
		return fa.getBalance();
	}
	
}

