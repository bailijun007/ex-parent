package com.hp.sh.expv3.pj.module.plate.constant;

import com.hp.sh.expv3.pj.module.plate.entity.PlateStrategy;

public class CacheKey {
	
	public static final String getSettingKey(PlateStrategy ps){
		String key = "project:plateStrategy:"+ps.getModule()+":"+ps.getAsset()+"__"+ps.getSymbol();
		return key;
	}
	
	public static final String getPriceKey(String asset){
		String key = "ticker:bb:lastPrice:"+asset;
		return key;
	}
	
}
