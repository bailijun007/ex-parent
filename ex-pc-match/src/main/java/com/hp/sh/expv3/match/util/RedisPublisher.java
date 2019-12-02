/**
 * @author zw
 * @date 2019/8/20
 */
package com.hp.sh.expv3.match.util;

import com.hp.sh.expv3.match.msg.BaseMessageDto;

public final class RedisPublisher {

    public final static void safeNotify(RedisUtil redisUtil, String channel, BaseMessageDto msg) {
        redisUtil.publish(channel, JsonUtil.toJsonString(msg));
    }

}