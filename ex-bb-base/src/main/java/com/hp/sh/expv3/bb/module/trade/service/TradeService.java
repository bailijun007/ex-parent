package com.hp.sh.expv3.bb.module.trade.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.bb.module.trade.dao.PcTradeDAO;
import com.hp.sh.expv3.bb.module.trade.entity.PcMatchedResult;

@Service
@Transactional(rollbackFor=Exception.class)
public class TradeService {
	@Autowired
	private PcTradeDAO pcTradeDAO;

	public void batchSave(List<PcMatchedResult> list) {
		for(PcMatchedResult pt:list){
			this.pcTradeDAO.save(pt);
		}
	}
	
}
