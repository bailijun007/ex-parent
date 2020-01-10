/**
 * @author zw
 * @date 2019/7/23
 */
package com.hp.sh.expv3.match.util;

import com.google.common.collect.ImmutableMap;

public final class BbRocketMqUtil {

    public static String buildBbOrderTopicName(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

    public static String buildBbAccountContractMqTopicName(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

    public static String buildBbOrderConsumerGroupName(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

    public static String buildBbOrderConsumerInstanceName(String pattern, String asset, String symbol) {
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

}