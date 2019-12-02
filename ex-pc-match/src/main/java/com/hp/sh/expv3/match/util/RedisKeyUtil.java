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

    public static String buildFundAccountChannelRedisKey(String pattern, String asset) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset));
    }

    public static String buildPcAccountChannelRedisKey(String pattern, String asset) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset));
    }

    public static String buildPcFeeRedisKey(String pattern) {
        return pattern;
    }

    public static String[] buildPcFeeRedisMakerTakerField(long makerAccountId, long takerAccountId) {
        return new String[]{buildPcFeeMakerField(makerAccountId), buildPcFeeTakerField(takerAccountId)};
    }

    public static String buildPcFeeMakerField(long accountId) {
        return "m_" + accountId;
    }

    public static String buildPcFeeTakerField(long accountId) {
        return "t_" + accountId;
    }

    public static String buildPcBookChannelRedisKey(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

    public static String buildPcTradeChannelRedisKey(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

    public static String buildPcOrderChannelRedisKey(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

    public static String buildPcPosChannelRedisKey(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

    public static String buildPcOrderSnapshotRedisKey(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

    public static String buildOrderSentMqOffsetRedisKeyPattern(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

}
