/**
 * @author zw
 * @date 2019/8/21
 */
package com.hp.sh.expv3.match.util;

import com.hp.sh.expv3.match.config.setting.RedisKeySetting;
import com.hp.sh.expv3.match.constant.PcmatchConst;
import com.hp.sh.expv3.match.msg.BookMsgDto;
import com.hp.sh.expv3.match.bo.PcOrderNotMatchedBo;
import com.hp.sh.expv3.match.msg.TradeListMsgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;

@Service
public class PcNotify {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisKeySetting redisKeySetting;

    @Autowired
    @Qualifier(PcmatchConst.MODULE_NAME + "RedisUtil")
    private RedisUtil pcRedisUtil;

    /**
     * 测试用，记录最新价格
     *
     * @param asset
     * @param symbol
     * @param lastPrice
     * @return
     */
    public boolean safeNotify(String asset, String symbol, BigDecimal lastPrice) {
        String lastPriceKey = RedisKeyUtil.buildLastPriceRedisKey(redisKeySetting.getPcLastPricePattern(), asset, symbol);
        pcRedisUtil.set(lastPriceKey, DecimalUtil.toTrimLiteral(lastPrice));
        String lastPriceHistoryKey = RedisKeyUtil.buildLastPriceRedisKey(redisKeySetting.getPcLastPriceHistoryPattern(), asset, symbol);
        long now = System.currentTimeMillis();
        String member = DecimalUtil.toTrimLiteral(lastPrice) + "#" + String.valueOf(now);
        pcRedisUtil.zadd(lastPriceHistoryKey, new HashMap<String, Double>() {{
            put(member, Double.valueOf(now).doubleValue());
        }});
        return true;
    }

    public boolean safeNotify(String asset, String symbol, PcOrderNotMatchedBo msg) {
        String channel = RedisKeyUtil.buildPcOrderChannelRedisKey(redisKeySetting.getPcOrderChannelRedisKeyPattern(), asset, symbol);
        if (logger.isDebugEnabled()) {
            logger.debug("{} {} send order:{}", asset, symbol, JsonUtil.toJsonString(msg));
        }
        RedisPublisher.safeNotify(pcRedisUtil, channel, msg);
        return true;
    }

    public boolean safeNotify(String asset, String symbol, BookMsgDto msg) {
        String channel = RedisKeyUtil.buildPcBookChannelRedisKey(redisKeySetting.getPcBookChannelRedisKeyPattern(), asset, symbol);
        if (logger.isDebugEnabled()) {
            logger.debug("{} {} send book:{}", asset, symbol, JsonUtil.toJsonString(msg));
        }
        RedisPublisher.safeNotify(pcRedisUtil, channel, msg);
        return true;
    }

    public boolean safeNotify(String asset, String symbol, TradeListMsgDto msg) {
        String channel = RedisKeyUtil.buildPcTradeChannelRedisKey(redisKeySetting.getPcTradeChannelRedisKeyPattern(), asset, symbol);
        if (logger.isDebugEnabled()) {
            logger.debug("{} {} send trade list:{}", asset, symbol, JsonUtil.toJsonString(msg));
        }
        RedisPublisher.safeNotify(pcRedisUtil, channel, msg);
        return true;
    }

}