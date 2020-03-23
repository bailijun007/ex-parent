
package com.hp.sh.expv3.pc.module.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.pc.module.order.dao.PcAccountLogDAO;
import com.hp.sh.expv3.pc.module.order.entity.PcAccountLog;
import com.hp.sh.expv3.utils.DbDateUtils;

/**
 * 
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class PcAccountLogService{

	@Autowired
	private PcAccountLogDAO pcAccountLogDAO;

	public void save(PcAccountLog pcAccountLog){
		Long now = DbDateUtils.now();
		this.pcAccountLogDAO.save(pcAccountLog);
	}

}
