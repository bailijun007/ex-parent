
package com.hp.sh.expv3.pc.module.symbol.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.pc.component.PcDefaultSymbolSetting;
import com.hp.sh.expv3.pc.module.symbol.dao.PcAccountSymbolDAO;
import com.hp.sh.expv3.pc.module.symbol.entity.PcAccountSymbol;
import com.hp.sh.expv3.pc.module.symbol.service.PcAccountSymbolService;

/**
 * 
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class PcAccountSymbolServiceImpl implements PcAccountSymbolService{

	@Autowired
	private PcAccountSymbolDAO pcAccountSymbolDAO;
	
	@Autowired
	private PcDefaultSymbolSetting pcDefaultSymbolSetting;

	@Override
	public void create(Long userId, String asset, String symbol){
		Date now = new Date();
		PcAccountSymbol entity = new PcAccountSymbol();
		entity.setAsset(asset);
		entity.setMarginMode(pcDefaultSymbolSetting.getMarginMode());
		entity.setMaxLeverage(pcDefaultSymbolSetting.getMaxLeverage());
		entity.setCrossLeverage(pcDefaultSymbolSetting.getCrossLeverage());
		entity.setLongLeverage(pcDefaultSymbolSetting.getLongLeverage());
		entity.setShortLeverage(pcDefaultSymbolSetting.getShortLeverage());
		entity.setHoldMarginRatio(pcDefaultSymbolSetting.getHoldMarginRatio());
		entity.setSymbol(symbol);
		entity.setUserId(userId);
		entity.setVersion(0L);
		entity.setModified(now);
		entity.setCreated(now);
		pcAccountSymbolDAO.save(entity );
	}
	
	@Override
	public PcAccountSymbol get(Long userId, String asset, String symbol){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("asset", asset);
		params.put("symbol", symbol);
		PcAccountSymbol as = this.pcAccountSymbolDAO.queryOne(params);
		return as;
	}

}
