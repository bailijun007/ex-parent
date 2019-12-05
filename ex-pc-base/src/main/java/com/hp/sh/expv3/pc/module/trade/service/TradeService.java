package com.hp.sh.expv3.pc.module.trade.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.pc.module.trade.dao.PcTradeDAO;
import com.hp.sh.expv3.pc.module.trade.entity.PcTrade;

@Service
@Transactional
public class TradeService {
	@Autowired
	private PcTradeDAO pcTradeDAO;

	public void batchSave(List<PcTrade> list) {
		for(PcTrade pt:list){
			this.pcTradeDAO.save(pt);
		}
	}
	
}
