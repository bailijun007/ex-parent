package com.hp.sh.expv3.pc.extension.mq;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.hp.sh.expv3.pc.extension.service.PcAccountLogExtendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.extension.constant.PcCommonErrorCode;
import com.hp.sh.expv3.pc.extension.dao.PcAccountLogDAO;
import com.hp.sh.expv3.pc.extension.vo.PcAccountLogVo;
import com.hp.sh.expv3.pc.msg.MsgConstant;
import com.hp.sh.expv3.pc.msg.PcAccountLog;
import com.hp.sh.rocketmq.annotation.MQListener;

@Component
@MQListener(topic = MsgConstant.EVENT_TOPIC, orderly = MQListener.ORDERLY_YES)
public class EventMqConsumer {
    private static final Logger logger = LoggerFactory.getLogger(EventMqConsumer.class);
    @Autowired
    private PcAccountLogExtendService pcAccountLogExtendService;

    public void handleMsg(PcAccountLog msg) {
        logger.info("收到消息:{}", msg);
        Optional<PcAccountLog> optional = Optional.ofNullable(msg);
        PcAccountLogVo vo = new PcAccountLogVo(optional.map(PcAccountLog::getType).orElse(null),
                optional.map(PcAccountLog::getUserId).orElse(null),
                optional.map(PcAccountLog::getAsset).orElse(null),
                optional.map(PcAccountLog::getSymbol).orElse(null),
                optional.map(PcAccountLog::getRefId).orElse(null),
                optional.map(PcAccountLog::getTime).orElse(null));
        PcAccountLogVo pcAccountLogVo = pcAccountLogExtendService.getPcAccountLog(vo);
        if (pcAccountLogVo == null) {
            pcAccountLogExtendService.save(vo);
        } else {
            logger.info("收到重复消息:{}", msg);
        }


    }

}
