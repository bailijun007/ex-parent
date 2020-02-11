package com.hp.sh.expv3.bb.module.trade.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.module.trade.dao.BBMatchedTradeDAO;
import com.hp.sh.expv3.bb.module.trade.entity.BBMatchedTrade;
import com.hp.sh.expv3.utils.IntBool;

@Service
@Transactional(rollbackFor=Exception.class)
public class BBMatchedTradeService {
	@Autowired
	private BBMatchedTradeDAO matchedTradeDAO;

	public List<BBMatchedTrade> queryPending(Page page, Long startTime, Long userId, Long startId) {
		List<BBMatchedTrade> list = this.matchedTradeDAO.queryPending(page, startTime, userId, startId);
		return list;
	}

	public void setMakerHandleStatus(Long id) {
		BBMatchedTrade matchedTrade = matchedTradeDAO.findById(id);
		matchedTrade.setMakerHandleStatus(IntBool.YES);
		this.matchedTradeDAO.update(matchedTrade);
	}

	public void setTakerHandleStatus(Long id) {
		BBMatchedTrade matchedTrade = matchedTradeDAO.findById(id);
		matchedTrade.setTakerHandleStatus(IntBool.YES);
		this.matchedTradeDAO.update(matchedTrade);
	}

	public void save(BBMatchedTrade matchedTrade) {
		this.matchedTradeDAO.save(matchedTrade);
	}
	
}
