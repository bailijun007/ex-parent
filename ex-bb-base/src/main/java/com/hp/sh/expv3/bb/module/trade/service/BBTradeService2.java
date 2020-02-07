package com.hp.sh.expv3.bb.module.trade.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.bb.module.trade.dao.BBTradeDAO;
import com.hp.sh.expv3.bb.module.trade.entity.BBMatchedTrade;

@Service
@Transactional(rollbackFor=Exception.class)
public class BBTradeService2 {
	@Autowired
	private BBTradeDAO bBTradeDAO;

	public void batchSave(List<BBMatchedTrade> list) {
		for(BBMatchedTrade pt:list){
			this.bBTradeDAO.save(pt);
		}
	}
	
}
