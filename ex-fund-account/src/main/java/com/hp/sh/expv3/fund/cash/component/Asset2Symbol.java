package com.hp.sh.expv3.fund.cash.component;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class Asset2Symbol {
    private static final String KEY="asset";
    private static final Map<String, Integer> asset2symbolMap = new HashMap<String, Integer>();
    private static final Map<Integer, String> symbol2assetMap = new HashMap<Integer, String>();

    @Resource(name = "templateDB0")
    private StringRedisTemplate templateDB0;


    static {
        // BTC btc symbol
        asset2symbolMap.put("BTC", 10000);
        asset2symbolMap.put("USDT", 10001);
        asset2symbolMap.put("ETH", 20000);
        asset2symbolMap.put("EUSDT", 20001);
        asset2symbolMap.put("BYS1", 20002);
        asset2symbolMap.put("BYS2", 20003);

        //  EOS 系列
        asset2symbolMap.put("EOS", 30000);
        asset2symbolMap.put("MainMax", 99999);

        // Test BTC Net
        asset2symbolMap.put("TBTC", 100000);
        asset2symbolMap.put("TUSDT", 100001);

        // Test Eth Net
        asset2symbolMap.put("TETH", 200000);
        asset2symbolMap.put("TEUSDT", 200001);
        asset2symbolMap.put("TBYS1", 200002);
        asset2symbolMap.put("TETHMAX", 299999);

        // Test EOS net
        asset2symbolMap.put("TEOS", 300000);
        asset2symbolMap.put("TEOSMAX", 399999);

        asset2symbolMap.forEach((k, v) -> symbol2assetMap.put(v, k));

    }


    //获取SymbolId
    public Integer getChainSymbolId(String asset) {
        HashOperations hashOperations = templateDB0.opsForHash();
        Object s = hashOperations.get(KEY, asset);
        if (null != s) {
            Map mapType = JSON.parseObject(s.toString(), Map.class);
            for (Object obj : mapType.keySet()){
                if(obj.toString().equals("chainSymbolId")){
                    String s1 = mapType.get(obj).toString();
                    return Integer.parseInt(s1);
                }
            }
        }

	    return null;
    }

    public Integer getSymbol(String asset) {
//        return asset2symbolMap.get(asset);
       return getChainSymbolId(asset);

    }

    public String getAsset(Integer symbol) {
        return symbol2assetMap.get(symbol);
    }

    public String getAsset(String symbol) {
        return symbol2assetMap.get(Integer.parseInt(symbol));
    }

}
