/**
 * @author zw
 * @date 2019/8/21
 */
package com.hp.sh.expv3.match.component.notify;

import com.hp.sh.expv3.match.config.setting.PcmatchRedisKeySetting;
import com.hp.sh.expv3.match.constant.PcmatchConst;
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
public class PcNotify {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PcmatchRedisKeySetting pcmatchRedisKeySetting;

    @Autowired
    @Qualifier(PcmatchConst.MODULE_NAME + "RedisUtil")
    private RedisUtil pcRedisUtil;

    public boolean safeNotify(String asset, String symbol, BookMsgDto msg) {
        String channel = RedisKeyUtil.buildPcBookChannelRedisKey(pcmatchRedisKeySetting.getPcBookChannelRedisKeyPattern(), asset, symbol);
        logger.info("{} {} send book:{}", asset, symbol, JsonUtil.toJsonString(msg));
        RedisPublisher.safeNotify(pcRedisUtil, channel, msg);
        return true;
    }

    public boolean safeNotify(String asset, String symbol, TradeListMsgDto msg) {
        String channel = RedisKeyUtil.buildPcTradeChannelRedisKey(pcmatchRedisKeySetting.getPcTradeChannelRedisKeyPattern(), asset, symbol);
        logger.info("{} {} send trade list:{}", asset, symbol, JsonUtil.toJsonString(msg));
        RedisPublisher.safeNotify(pcRedisUtil, channel, msg);
        return true;
    }

}