package com.hp.sh.expv3.pj.module.plate.component;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.cache.Cache;
import com.gitee.hupadev.commons.json.JsonUtils;
import com.hp.sh.expv3.pj.module.plate.constant.CacheKey;
import com.hp.sh.expv3.pj.module.plate.entity.PlateStrategy;

@Component
@SuppressWarnings("all")
public class CacheService {
	
	@Autowired
	private Cache cache;
	
	@Resource(name="templateDB15")
	private RedisTemplate<String,String> redisTemplate;
	
	public PlateStrategy getCachedPlateStrategy(String asset, String symbol) {
		String key = "project:plateStrategy:"+asset+":"+symbol;
		String json = (String) cache.get(key);
		if(StringUtils.isNotBlank(json)){
			PlateStrategy ps = JsonUtils.toObject(json, PlateStrategy.class);
			return ps;
		}
		return null;
	}
	
	public void cachePlateStrategy(PlateStrategy ps) {
		String key = "project:plateStrategy:"+ps.getAsset()+":"+ps.getSymbol();
		cache.put(key, ps);
	}
	
	public void cacheNewPrice(String asset, String symbol, BigDecimal newPrice){
		String key = CacheKey.getPriceKey(asset);
		HashOperations ops = redisTemplate.opsForHash();
		ops.put(key, symbol, ""+newPrice);
	}
	
	public Double getCachedNewPrice(String asset, String symbol){
		String key = CacheKey.getPriceKey(asset);
		HashOperations ops = redisTemplate.opsForHash();
		String val = (String) ops.get(key, symbol);
		if(StringUtils.isBlank(val)){
			return null;
		}
		return Double.parseDouble(val);
	}
	
}
