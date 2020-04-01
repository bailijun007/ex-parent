package com.hp.sh.expv3.bb.trade.mq;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.trade.constant.MsgConstant;
import com.hp.sh.expv3.bb.trade.pojo.BbMatchExtVo;
import com.hp.sh.expv3.bb.trade.service.BbMatchExtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.hp.sh.rocketmq.annotation.MQListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class BbMatchMqConsumer {
    private static final Logger logger = LoggerFactory.getLogger(BbMatchMqConsumer.class);
    @Autowired
    private BbMatchExtService bbMatchExtService;

    @MQListener(tags = MsgConstant.TAG_BB_MATCH)
    public void handleMsg(List<BbMatchExtVo> msg) {
        logger.info("收到bb_match撮合推送消息:{}", msg);
        try {
            bbMatchExtService.batchSave(msg);
        } catch (Exception e) {
            logger.error("error msg:{}", JSON.toJSONString(msg));
        }

    }

}
