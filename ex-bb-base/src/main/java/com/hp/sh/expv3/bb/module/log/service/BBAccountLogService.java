
package com.hp.sh.expv3.bb.module.log.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.bb.module.log.dao.BBAccountLogDAO;
import com.hp.sh.expv3.bb.module.log.entity.BBAccountLog;

/**
 * 
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class BBAccountLogService{

	@Autowired
	private BBAccountLogDAO bBAccountLogDAO;

	public void save(BBAccountLog bBAccountLog){
		this.bBAccountLogDAO.save(bBAccountLog);
	}
	
}
