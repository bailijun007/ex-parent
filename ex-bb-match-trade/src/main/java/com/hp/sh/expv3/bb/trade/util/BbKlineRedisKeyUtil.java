package com.hp.sh.expv3.bb.trade.util;


import java.util.HashMap;

/**
 * @author BaiLiJun  on 2020/3/11
 */
public final class BbKlineRedisKeyUtil {

    public static String buildPersistentDataUpdateEventKey(String pattern,String asset, String symbol, int freq) {
        String persistentDataUpdateEventKey = StringReplaceUtil.replace(pattern, new HashMap<String, String>() {
            {
                put("asset", asset);
                put("symbol", symbol);
                put("freq", freq + "");
            }
        });
        return persistentDataUpdateEventKey;
    }

    public static String buildKlineDataRedisKey(String pattern, String asset, String symbol, int frequency) {
        return StringReplaceUtil.replace(pattern, new HashMap<String, String>() {{
            put("asset", asset);
            put("symbol", symbol);
            put("freq", "" + frequency);
        }});
    }





}
