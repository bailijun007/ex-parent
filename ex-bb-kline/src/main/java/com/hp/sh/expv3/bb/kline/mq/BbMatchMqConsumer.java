package com.hp.sh.expv3.bb.kline.mq;
import com.hp.sh.expv3.bb.kline.constant.MsgConstant;
import com.hp.sh.expv3.bb.kline.service.BbMatchExtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.hp.sh.rocketmq.annotation.MQListener;

@Component
public class BbMatchMqConsumer {
    private static final Logger logger = LoggerFactory.getLogger(BbMatchMqConsumer.class);
    @Autowired
    private BbMatchExtService bbMatchExtService;

    @MQListener(topic = MsgConstant.EVENT_TOPIC, orderly = MQListener.ORDERLY_YES)
    public void handleMsg(/*PcAccountLog msg*/) {
//        logger.info("收到消息:{}", msg);
//        PcAccountLogVo vo = new PcAccountLogVo();
//        BeanUtils.copyProperties(msg,vo);
//        PcAccountLogVo pcAccountLogVo = pcAccountLogExtendService.getPcAccountLog(vo);
//        if (pcAccountLogVo == null) {
//            pcAccountLogExtendService.save(vo);
//        } else {
//            logger.info("收到重复消息:{}", msg);
//        }


    }

}
