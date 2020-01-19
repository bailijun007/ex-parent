package com.hp.sh.expv3.fund.cash.component;

import com.alibaba.fastjson.JSON;
import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.fund.cash.entity.AssetVo;
import com.hp.sh.expv3.fund.cash.entity.DepositRecord;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Asset2Symbol {
    private static final String KEY = "asset";
    private Map<String, Integer> symbol2SymbolId;
    private Map<Integer, String> symbolId2Symbol;

    @Resource(name = "templateDB0")
    private StringRedisTemplate templateDB0;

    @PostConstruct
    private void init() {
        buildMap();
    }

    //获取SymbolId
    public Integer getChainSymbolId(String symbol) {
        return symbol2SymbolId.get(symbol);
    }

    public String getSymbol(Integer symbolId) {
        return symbolId2Symbol.get(symbolId);
    }

    public void buildMap() {
        HashOperations hashOperations = templateDB0.opsForHash();
        final Map<String, String> entries = hashOperations.entries(KEY);

        final Map<String, Integer> symbol2SymbolIdMap = new HashMap<>();
        final Map<Integer, String> symbolId2SymbolMap = new HashMap<>();

        for (Map.Entry<String, String> asset2AssetVo : entries.entrySet()) {
            String asset = asset2AssetVo.getKey();
            AssetVo assetVo = JSON.parseObject(asset2AssetVo.getValue(), AssetVo.class);
            symbol2SymbolIdMap.put(asset, assetVo.getChainSymbolId());
            symbolId2SymbolMap.put(assetVo.getChainSymbolId(), asset);
        }

        symbol2SymbolId = symbol2SymbolIdMap;
        symbolId2Symbol = symbolId2SymbolMap;
    }


    @Scheduled(cron = "0 0/1 * * * ?")
    public void handlePendingSynch() {
        buildMap();
    }

}