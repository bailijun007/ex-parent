/**
 * @author zw
 * @date 2019/8/21
 */
package com.hp.sh.expv3.match.component.notify;

import com.hp.sh.expv3.match.config.setting.BbmatchRedisKeySetting;
import com.hp.sh.expv3.match.constant.BbmatchConst;
import com.hp.sh.expv3.match.msg.BookMsgDto;
import com.hp.sh.expv3.match.msg.TradeListMsgDto;
import com.hp.sh.expv3.match.util.JsonUtil;
import com.hp.sh.expv3.match.util.RedisKeyUtil;
import com.hp.sh.expv3.match.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class BbNotify {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BbmatchRedisKeySetting bbmatchRedisKeySetting;

    @Autowired
    @Qualifier(BbmatchConst.MODULE_NAME + "RedisUtil")
    private RedisUtil bbRedisUtil;

    public boolean safeNotify(String asset, String symbol, BookMsgDto msg) {
        String channel = RedisKeyUtil.buildBbBookChannelRedisKey(bbmatchRedisKeySetting.getBbBookChannelRedisKeyPattern(), asset, symbol);
        RedisPublisher.safeNotify(bbRedisUtil, channel, msg);
        logger.info("{} {} send book:{}", asset, symbol, JsonUtil.toJsonString(msg));
        return true;
    }

    public boolean safeNotify(String asset, String symbol, TradeListMsgDto msg) {
        String channel = RedisKeyUtil.buildBbTradeChannelRedisKey(bbmatchRedisKeySetting.getBbTradeChannelRedisKeyPattern(), asset, symbol);
        RedisPublisher.safeNotify(bbRedisUtil, channel, msg);
        logger.info("{} {} send trade list:{}", asset, symbol, JsonUtil.toJsonString(msg));
        return true;
    }

}