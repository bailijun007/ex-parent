
package com.hp.sh.expv3.bb.module.symbol.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.bb.component.BBDefaultSymbolSetting;
import com.hp.sh.expv3.bb.module.symbol.dao.BBAccountSymbolDAO;
import com.hp.sh.expv3.bb.module.symbol.entity.BBAccountSymbol;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.IntBool;

/**
 * 
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class BBAccountSymbolService{

	@Autowired
	private BBAccountSymbolDAO bBAccountSymbolDAO;
	
	@Autowired
	private BBDefaultSymbolSetting bBDefaultSymbolSetting;
    @Autowired
    private ApplicationEventPublisher publisher;

	@LockIt(key="${userId}-${asset}-${symbol}")
	public BBAccountSymbol create(Long userId, String asset, String symbol){
		BBAccountSymbol as = this.get(userId, asset, symbol);
		if(as!=null){
			return as;
		}
		return this.doCreate(userId, asset, symbol);
	}
	
	private BBAccountSymbol doCreate(Long userId, String asset, String symbol){
		Long now = DbDateUtils.now();
		BBAccountSymbol entity = new BBAccountSymbol();
		entity.setAsset(asset);
		entity.setMarginMode(bBDefaultSymbolSetting.getMarginMode());
		entity.setLongMaxLeverage(bBDefaultSymbolSetting.getLongMaxLeverage(asset, symbol));
		entity.setShortMaxLeverage(bBDefaultSymbolSetting.getShortMaxLeverage(asset, symbol));
		entity.setCrossLeverage(bBDefaultSymbolSetting.getCrossLeverage(asset, symbol));
		entity.setLongLeverage(bBDefaultSymbolSetting.getLongLeverage(asset, symbol));
		entity.setShortLeverage(bBDefaultSymbolSetting.getShortLeverage(asset, symbol));
		entity.setCrossMaxLeverage(bBDefaultSymbolSetting.getCrossMaxLeverage(asset, symbol));
		entity.setSymbol(symbol);
		entity.setUserId(userId);
		entity.setVersion(0L);
		entity.setModified(now);
		entity.setCreated(now);
		bBAccountSymbolDAO.save(entity );
		return entity;
	}
	
	private BBAccountSymbol get(Long userId, String asset, String symbol){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("asset", asset);
		params.put("symbol", symbol);
		BBAccountSymbol as = this.bBAccountSymbolDAO.queryOne(params);
		return as;
	}
	
	public BBAccountSymbol getOrCreate(Long userId, String asset, String symbol){
		BBAccountSymbol as = this.get(userId, asset, symbol);
		if(as==null){
			as = this.doCreate(userId, asset, symbol);
		}
		return as;
	}
	
	public BigDecimal getLeverage(long userId, String asset, String symbol, int longFlag){
		BBAccountSymbol as = this.getOrCreate(userId, asset, symbol);
		if(IntBool.isTrue(longFlag)){	//多
			return as.getLongLeverage();
		}else{		//空
			return as.getShortLeverage();
		}
	}

	public void changeMarginMode(Long userId, String asset, String symbol, Integer marginMode) {
		BBAccountSymbol as = this.getOrCreate(userId, asset, symbol);
		if(!as.getMarginMode().equals(marginMode)){
			as.setMarginMode(marginMode);
			this.update(as);
		}
	}

	public void update(BBAccountSymbol accountSymbol) {
        Long now = DbDateUtils.now();
		accountSymbol.setModified(now);
		this.bBAccountSymbolDAO.update(accountSymbol);
		publisher.publishEvent(accountSymbol);
	}

}
