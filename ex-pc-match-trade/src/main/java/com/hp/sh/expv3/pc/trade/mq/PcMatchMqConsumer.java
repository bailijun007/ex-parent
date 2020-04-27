package com.hp.sh.expv3.pc.trade.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hp.sh.expv3.config.redis.RedisUtil;
import com.hp.sh.expv3.pc.trade.constant.MsgConstant;
import com.hp.sh.expv3.pc.trade.pojo.PcMatchData;
import com.hp.sh.expv3.pc.trade.pojo.PcMatchExtVo;
import com.hp.sh.expv3.pc.trade.service.PcMatchExtService;
import com.hp.sh.rocketmq.annotation.MQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PcMatchMqConsumer {
    private static final Logger logger = LoggerFactory.getLogger(PcMatchMqConsumer.class);
    @Autowired
    private PcMatchExtService bbMatchExtService;

    @Value("${pc.trade.table}")
    private String table;

    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @MQListener(tags = MsgConstant.TAG_PC_MATCH)
    public void handleMsg(Object msg) {
        String jsonString = JSON.toJSONString(msg);
        logger.info("收到pc_match撮合推送消息:{}", jsonString);

        List<PcMatchExtVo> match = null;
        Object json = JSONObject.parse(jsonString);
        if (json instanceof JSONObject) {
            //对象格式
            PcMatchData bbMatchData = JSON.parseObject(jsonString, PcMatchData.class);
            match = bbMatchData.getMatch();
        }else if (json instanceof JSONArray){
            //数组格式
            match = JSON.parseArray(jsonString, PcMatchExtVo.class);
        }

        if (CollectionUtils.isEmpty(match)) {
            return;
        }
        try {
//            bbMatchExtService.batchSave(msg,table);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate localDate = LocalDate.now();
            String format = localDate.format(dtf);
            PcMatchExtVo pcMatchExtVo = match.get(0);
            String asset = pcMatchExtVo.getAsset();
            String symbol = pcMatchExtVo.getSymbol();

            int size = bbMatchExtService.batchSave(match, table);
            String key = "pc:matchCount:" + asset + ":" + symbol + ":" + format;
            metadataRedisUtil.incrBy(key, size);
        } catch (Exception e) {
            logger.error("error msg:{}", JSON.toJSONString(msg));
        }

    }

}
