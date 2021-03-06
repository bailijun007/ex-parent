/**
 * @author 10086
 * @date 2019/10/30
 */
package com.hp.sh.expv3.match.util;

import com.google.common.collect.ImmutableMap;

public class RedisKeyUtil {

    public static String buildAccountChannelRedisKey(String pattern) {
        return pattern;
    }

    public static String buildMarkPriceRedisKey(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

    public static String buildLastPriceRedisKey(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

    public static String buildBbAccountChannelRedisKey(String pattern, String asset) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset));
    }

    public static String buildBbBookChannelRedisKey(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

    public static String buildBbTradeChannelRedisKey(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

    public static String buildBbOrderMatchedQueueRedisKey(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

    public static String buildBbOrderChannelRedisKey(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

    public static String buildBbOrderSnapshotRedisKey(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

    public static String buildOrderSentMqOffsetRedisKeyPattern(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

}
