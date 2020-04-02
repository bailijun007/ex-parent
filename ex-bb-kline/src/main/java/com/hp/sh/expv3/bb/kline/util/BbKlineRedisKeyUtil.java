package com.hp.sh.expv3.bb.kline.util;

import com.hp.sh.expv3.bb.kline.constant.BbextendConst;

import java.util.HashMap;

/**
 * @author BaiLiJun  on 2020/3/11
 */
public final class BbKlineRedisKeyUtil {

    public static String buildKlineDataRedisKey(String pattern, String asset, String symbol, int frequency) {
        return StringReplaceUtil.replace(pattern, new HashMap<String, String>() {{
            put("asset", asset);
            put("symbol", symbol);
            put("freq", "" + frequency);
        }});
    }

    public static String buildThirdDataRedisKey(String pattern, String asset, String symbol, int frequency) {
        return StringReplaceUtil.replace(pattern, new HashMap<String, String>() {{
            put("asset", asset);
            put("symbol", symbol);
            put("freq", "" + frequency);
        }});
    }


    public static String buildKlinePersistentDataRedisKey(String pattern, String asset, String symbol, int frequency) {
        return StringReplaceUtil.replace(pattern, new HashMap<String, String>() {
            {
                put("asset", asset);
                put("symbol", symbol);
                put("freq", "" + frequency);
            }
        });
    }


    public static String buildKlineUpdateEventRedisKey(String pattern, String asset, String symbol, int frequency) {
//        bb.kline.update
        return StringReplaceUtil.replace(pattern, new HashMap<String, String>() {
            {
                put("asset", asset);
                put("symbol", symbol);
                put("freq", "" + frequency);
            }
        });
    }

    public static String buildUpdateRedisMember(String asset, String symbol, int frequency, long ms) {
        return StringReplaceUtil.replace(BbextendConst.BB_KLINE_UPDATE_MEMBER, new HashMap<String, String>() {{
            put("asset", asset);
            put("symbol", symbol);
            put("freq", "" + frequency);
            put("ms", "" + ms);
        }});
    }


    public static String buildFromExpBbKlineTaskRedisKey(String pattern, String asset, String symbol, int freq) {
        String BbKlineFromExpTaskRedisKey = StringReplaceUtil.replace(pattern, new HashMap<String, String>() {
            {
                put("asset", asset);
                put("symbol", symbol);
                put("freq", freq + "");
            }
        });
        return BbKlineFromExpTaskRedisKey;
    }




    public static String buildFromExpBbKlineDataByTradeRedisKey(String pattern, String asset, String symbol, int frequency) {
        return StringReplaceUtil.replace(pattern, new HashMap<String, String>() {{
            put("asset", asset);
            put("symbol", symbol);
            put("freq", "" + frequency);
        }});
    }
    public static String buildFromExpBbKlineUpdateEventKey(String pattern,String asset, String symbol, int freq) {
        String bbKlineFromExpUpdateKey = StringReplaceUtil.replace(pattern, new HashMap<String, String>() {
            {
                put("asset", asset);
                put("symbol", symbol);
                put("freq", freq + "");
            }
        });
        return bbKlineFromExpUpdateKey;
    }

    public static String buildKline2TradeTaskEventKey(String pattern, String asset, String symbol, int freq) {
        String bbKlineFromExpUpdateKey = StringReplaceUtil.replace(pattern, new HashMap<String, String>() {
            {
                put("asset", asset);
                put("symbol", symbol);
                put("freq", freq + "");
            }
        });
        return bbKlineFromExpUpdateKey;
    }




}
