package com.hp.sh.expv3.pc.extension.mq;

import java.util.HashMap;
import java.util.Map;

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
    private PcAccountLogDAO pcAccountLogDAO;

    public void handleMsg(PcAccountLog msg) {
        logger.info("收到消息:{}", msg);
        Map<String, Object> map = new HashMap<>();
        map.put("type", msg.getType());
        map.put("refId", msg.getRefId());
        map.put("userId", msg.getUserId());
        map.put("asset", msg.getAsset());
        map.put("symbol", msg.getSymbol());
        PcAccountLogVo pcAccountLogVo = pcAccountLogDAO.queryOne(map);
        if(pcAccountLogVo==null){
            PcAccountLogVo vo=new PcAccountLogVo(msg.getType(),msg.getUserId(),msg.getAsset(),msg.getSymbol(),msg.getRefId(),msg.getTime());
            int count = pcAccountLogDAO.save(vo);
            if(count!=1){
               throw new  ExException(PcCommonErrorCode.SAVE_PC_ACCOUNT_LOG_FAIL);
            }
        }else {
            logger.info("收到重复消息:{}", msg);
        }


    }

}
