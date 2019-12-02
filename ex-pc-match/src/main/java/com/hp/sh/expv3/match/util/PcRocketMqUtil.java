/**
 * @author zw
 * @date 2019/7/23
 */
package com.hp.sh.expv3.match.util;

import com.google.common.collect.ImmutableMap;

public final class PcRocketMqUtil {

    public static String buildPcOrderTopicName(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

    public static String buildPcAccountContractMqTopicName(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

    public static String buildPcOrderConsumerGroupName(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

    public static String buildPcOrderConsumerInstanceName(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

}