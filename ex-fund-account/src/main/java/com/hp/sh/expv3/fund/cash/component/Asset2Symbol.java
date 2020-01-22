package com.hp.sh.expv3.fund.cash.component;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.fund.cash.entity.AssetVo;

@Component
public class Asset2Symbol {
    private static final String KEY = "asset";

    @Resource(name = "templateDB0")
    private StringRedisTemplate templateDB0;

	private Map<String,Integer> asset2symbolMap = new HashMap<String,Integer>();
	private Map<Integer, String> symbol2assetMap = new HashMap<Integer, String>();
	
	public Integer getSymbol(String asset){
		return asset2symbolMap.get(asset);
	}
	
	public String getAsset(Integer symbol){
		return symbol2assetMap.get(symbol); 
	}

    public void buildMap() {
        HashOperations<String, String, String> hashOperations = templateDB0.opsForHash();
        final Map<String, String> entries = hashOperations.entries(KEY);

        final Map<String, Integer> asset2symbolMap = new HashMap<>();
        final Map<Integer, String> symbol2assetMap = new HashMap<>();

        for (Map.Entry<String, String> asset2AssetVo : entries.entrySet()) {
            String asset = asset2AssetVo.getKey();
            AssetVo assetVo = JSON.parseObject(asset2AssetVo.getValue(), AssetVo.class);
            asset2symbolMap.put(asset, assetVo.getChainSymbolId());
            symbol2assetMap.put(assetVo.getChainSymbolId(), asset);
        }

        this.asset2symbolMap = asset2symbolMap;
        this.symbol2assetMap = symbol2assetMap;
        
    }

    @PostConstruct
    private void init() {
        buildMap();
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void handlePendingSynch() {
        buildMap();
    }

}