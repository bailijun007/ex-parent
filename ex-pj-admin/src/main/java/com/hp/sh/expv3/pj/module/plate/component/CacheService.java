package com.hp.sh.expv3.pj.module.plate.component;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.json.JsonUtils;
import com.hp.sh.expv3.pj.module.plate.constant.CacheKey;
import com.hp.sh.expv3.pj.module.plate.entity.PlateStrategy;

@Component
@SuppressWarnings("all")
public class CacheService {
	
	@Resource(name="templateDB15")
	private RedisTemplate<String,String> redisTemplate;
	
	public PlateStrategy getCachedPlateStrategy(String asset, String symbol) {
		String key = "project:plateStrategy:"+asset+":"+symbol;
		ValueOperations<String, String> ops = redisTemplate.opsForValue();
		String json = ops.get(key);
		if(StringUtils.isNotBlank(json)){
			PlateStrategy ps = JsonUtils.toObject(json, PlateStrategy.class);
			return ps;
		}
		return null;
	}
	
	public void cachePlateStrategy(PlateStrategy ps) {
		String key = "project:plateStrategy:"+ps.getAsset()+":"+ps.getSymbol();
		ValueOperations<String, String> ops = redisTemplate.opsForValue();
		ops.set(key, JsonUtils.toJson(ps));
	}
	
	public void cacheNewestPrice(String asset, String symbol, BigDecimal newPrice){
		String key = CacheKey.getPriceKey(asset);
		HashOperations ops = redisTemplate.opsForHash();
		ops.put(key, symbol, ""+newPrice);
	}
	
	public Double getCachedNewestPrice(String asset, String symbol){
		String key = CacheKey.getPriceKey(asset);
		HashOperations ops = redisTemplate.opsForHash();
		String val = (String) ops.get(key, symbol);
		if(StringUtils.isBlank(val)){
			return null;
		}
		return Double.parseDouble(val);
	}
	
}
