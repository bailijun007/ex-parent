
package com.hp.sh.expv3.pc.module.symbol.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.base.exceptions.CommonError;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.pc.component.PcDefaultSymbolSetting;
import com.hp.sh.expv3.pc.module.symbol.dao.PcAccountSymbolDAO;
import com.hp.sh.expv3.pc.module.symbol.entity.PcAccountSymbol;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.IntBool;

/**
 * 
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class PcAccountSymbolService{

	@Autowired
	private PcAccountSymbolDAO pcAccountSymbolDAO;
	
	@Autowired
	private PcDefaultSymbolSetting pcDefaultSymbolSetting;

	@LockIt(key="${userId}-${asset}-${symbol}")
	public PcAccountSymbol create(Long userId, String asset, String symbol){
		PcAccountSymbol as = this.get(userId, asset, symbol);
		if(as!=null){
			return as;
		}
		Long now = DbDateUtils.now();
		PcAccountSymbol entity = new PcAccountSymbol();
		entity.setAsset(asset);
		entity.setMarginMode(pcDefaultSymbolSetting.getMarginMode());
		entity.setLongMaxLeverage(pcDefaultSymbolSetting.getLongMaxLeverage(asset, symbol));
		entity.setShortMaxLeverage(pcDefaultSymbolSetting.getShortMaxLeverage(asset, symbol));
		entity.setCrossLeverage(pcDefaultSymbolSetting.getCrossLeverage(asset, symbol));
		entity.setLongLeverage(pcDefaultSymbolSetting.getLongLeverage(asset, symbol));
		entity.setShortLeverage(pcDefaultSymbolSetting.getShortLeverage(asset, symbol));
		entity.setCrossMaxLeverage(pcDefaultSymbolSetting.getCrossMaxLeverage(asset, symbol));
		entity.setSymbol(symbol);
		entity.setUserId(userId);
		entity.setVersion(0L);
		entity.setModified(now);
		entity.setCreated(now);
		pcAccountSymbolDAO.save(entity );
		return entity;
	}
	
	public PcAccountSymbol get(Long userId, String asset, String symbol){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("asset", asset);
		params.put("symbol", symbol);
		PcAccountSymbol as = this.pcAccountSymbolDAO.queryOne(params);
		return as;
	}
	
	public BigDecimal getLeverage(long userId, String asset, String symbol, int longFlag){
		PcAccountSymbol as = this.get(userId, asset, symbol);
		if(as==null){
			as = this.create(userId, asset, symbol);
		}
		if(IntBool.isTrue(longFlag)){	//多
			return as.getLongLeverage();
		}else{		//空
			return as.getShortLeverage();
		}
	}

	public void changeMarginMode(Long userId, String asset, String symbol, Integer marginMode) {
		PcAccountSymbol as = this.get(userId, asset, symbol);
		if(!as.getMarginMode().equals(marginMode)){
			as.setMarginMode(marginMode);
			this.pcAccountSymbolDAO.update(as);
		}
	}

}
