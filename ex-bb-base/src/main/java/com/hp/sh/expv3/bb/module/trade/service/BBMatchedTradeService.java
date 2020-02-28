package com.hp.sh.expv3.bb.module.trade.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.module.trade.dao.BBMatchedTradeDAO;
import com.hp.sh.expv3.bb.module.trade.entity.BBMatchedTrade;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.IntBool;

@Service
@Transactional(rollbackFor=Exception.class)
public class BBMatchedTradeService {
	@Autowired
	private BBMatchedTradeDAO matchedTradeDAO;

	public List<BBMatchedTrade> queryPending(Page page, Long userId, Long startTime, Long startId) {
		List<BBMatchedTrade> list = this.matchedTradeDAO.queryPending(page, userId, startTime, startId);
		return list;
	}

	public void setMakerHandleStatus(Long id) {
		Long now = DbDateUtils.now();
		this.matchedTradeDAO.setMakerHandleStatus(id, IntBool.YES, now);
	}

	public void setTakerHandleStatus(Long id) {
		Long now = DbDateUtils.now();
		this.matchedTradeDAO.setTakerHandleStatus(id, IntBool.YES, now);
	}
	
	public boolean exist(Long mkOrderId, Long tkOrderId){
		long c = this.matchedTradeDAO.exist(mkOrderId, tkOrderId);
		return c > 0;
	}

	public void save(BBMatchedTrade matchedTrade) {
		Long now = DbDateUtils.now();
		matchedTrade.setCreated(now);
		matchedTrade.setModified(now);
		this.matchedTradeDAO.save(matchedTrade);
	}
	
}
