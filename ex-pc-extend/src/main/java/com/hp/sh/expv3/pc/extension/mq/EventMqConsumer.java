package com.hp.sh.expv3.pc.extension.mq;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.hp.sh.expv3.pc.extension.service.PcAccountLogExtendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.constant.MsgConstant;
import com.hp.sh.expv3.pc.extension.dao.PcAccountLogDAO;
import com.hp.sh.expv3.pc.extension.error.PcCommonErrorCode;
import com.hp.sh.expv3.pc.extension.vo.PcAccountLogVo;
import com.hp.sh.expv3.pc.msg.PcAccountLog;
import com.hp.sh.rocketmq.annotation.MQListener;

@Component
public class EventMqConsumer {
    private static final Logger logger = LoggerFactory.getLogger(EventMqConsumer.class);
    @Autowired
    private PcAccountLogExtendService pcAccountLogExtendService;

    @MQListener(topic = MsgConstant.EVENT_TOPIC, orderly = MQListener.ORDERLY_YES)
    public void handleMsg(PcAccountLog msg) {
        logger.info("收到消息:{}", msg);
        PcAccountLogVo vo = new PcAccountLogVo();
        BeanUtils.copyProperties(msg,vo);
        PcAccountLogVo pcAccountLogVo = pcAccountLogExtendService.getPcAccountLog(vo);
        if (pcAccountLogVo == null) {
            pcAccountLogExtendService.save(vo);
        } else {
            logger.info("收到重复消息:{}", msg);
        }


    }

}
