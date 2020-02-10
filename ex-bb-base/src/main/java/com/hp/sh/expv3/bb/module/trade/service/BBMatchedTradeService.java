package com.hp.sh.expv3.bb.module.trade.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.bb.module.trade.dao.BBMatchedTradeDAO;
import com.hp.sh.expv3.bb.module.trade.entity.BBMatchedTrade;

@Service
@Transactional(rollbackFor=Exception.class)
public class BBMatchedTradeService {
	@Autowired
	private BBMatchedTradeDAO matchedTradeDAO;

	public List<BBMatchedTrade> queryPending(Long userId) {
		
		return null;
	}

	public void setFinishStatus(Long id, Long userId) {
		
	}
	
}
