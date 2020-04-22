package com.hp.sh.expv3.pc.trade.mq;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.config.redis.RedisUtil;
import com.hp.sh.expv3.pc.trade.constant.MsgConstant;
import com.hp.sh.expv3.pc.trade.pojo.PcMatchExtVo;
import com.hp.sh.expv3.pc.trade.service.PcMatchExtService;
import com.hp.sh.rocketmq.annotation.MQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class BbMatchMqConsumer {
    private static final Logger logger = LoggerFactory.getLogger(BbMatchMqConsumer.class);
    @Autowired
    private PcMatchExtService bbMatchExtService;

    @Value("${pc.trade.table}")
    private String table;

    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @MQListener(tags = MsgConstant.TAG_PC_MATCH)
    public void handleMsg(List<PcMatchExtVo> msg) {
        logger.info("收到pc_match撮合推送消息:{}", msg);
        try {
//            bbMatchExtService.batchSave(msg,table);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate localDate = LocalDate.now();
            String format = localDate.format(dtf);
            PcMatchExtVo pcMatchExtVo = msg.get(0);
            String asset = pcMatchExtVo.getAsset();
            String symbol = pcMatchExtVo.getSymbol();

            int size = bbMatchExtService.batchSave(msg, table);
            String key = "pc:matchCount:" + pcMatchExtVo.getAsset() + ":" + pcMatchExtVo.getSymbol() + ":" + format;
            metadataRedisUtil.incrBy(key, size);
        } catch (Exception e) {
            logger.error("error msg:{}", JSON.toJSONString(msg));
        }

    }

}
