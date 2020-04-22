package com.hp.sh.expv3.bb.trade.mq;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.trade.constant.MsgConstant;
import com.hp.sh.expv3.bb.trade.pojo.BbMatchExtVo;
import com.hp.sh.expv3.bb.trade.service.BbMatchExtService;
import com.hp.sh.expv3.config.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.hp.sh.rocketmq.annotation.MQListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class BbMatchMqConsumer {
    private static final Logger logger = LoggerFactory.getLogger(BbMatchMqConsumer.class);
    @Autowired
    private BbMatchExtService bbMatchExtService;

    @Value("${bb.trade.table}")
    private String table;

    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @MQListener(tags = MsgConstant.TAG_BB_MATCH)
    public void handleMsg(List<BbMatchExtVo> msg) {
        logger.info("收到bb_match撮合推送消息:{}", msg);
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate localDate = LocalDate.now();
            String format = localDate.format(dtf);
            BbMatchExtVo bbMatchExtVo = msg.get(0);
            String asset = bbMatchExtVo.getAsset();
            String symbol = bbMatchExtVo.getSymbol();

            int size = bbMatchExtService.batchSave(msg, table);
            String key = "bb:matchCount:" + asset + ":" + symbol + ":" + format;
            metadataRedisUtil.incrBy(key, size);
        } catch (Exception e) {
            logger.error("error msg:{}", JSON.toJSONString(msg));
        }

    }

}
