
package com.hp.sh.expv3.bb.module.fail.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.bb.module.fail.dao.BBMqMsgDAO;
import com.hp.sh.expv3.bb.module.fail.entity.BBMqMsg;
import com.hp.sh.expv3.utils.DbDateUtils;

/**
 * 
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class BBMqMsgService{

	@Autowired
	private BBMqMsgDAO bBMqMsgDAO;

	public BBMqMsg findById(Long userId, String messageId) {
		return this.bBMqMsgDAO.findById(userId, messageId);
	}

	public void save(BBMqMsg bBMqMsg){
		Long now = DbDateUtils.now();
		bBMqMsg.setCreated(now);
		this.bBMqMsgDAO.save(bBMqMsg);
	}

	public void delete(Long userId, String messageId){
		this.bBMqMsgDAO.delete(userId, messageId);
	}

}
