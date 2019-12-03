/**
 * @author 10086
 * @date 2019/10/21
 */
package com.hp.sh.expv3.match.component.notify;

import com.hp.sh.expv3.match.component.rocketmq.BasePcOrderProducer;
import com.hp.sh.expv3.match.config.setting.PcmatchRocketMqSetting;
import com.hp.sh.expv3.match.enums.RmqTagEnum;
import com.hp.sh.expv3.match.mqmsg.PcMatchOrderSnapshotMqMsgDto;
import com.hp.sh.expv3.match.util.JsonUtil;
import com.hp.sh.expv3.match.util.PcRocketMqUtil;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PcOrderMqNotify {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BasePcOrderProducer orderProducer;

    @Autowired
    private PcmatchRocketMqSetting pcmatchRocketMqSetting;

    public boolean sendOrderSnapshotTrigger(String asset, String symbol) {
        String topic = PcRocketMqUtil.buildPcOrderTopicName(pcmatchRocketMqSetting.getPcOrderTopicNamePattern(), asset, symbol);

        PcMatchOrderSnapshotMqMsgDto msg = new PcMatchOrderSnapshotMqMsgDto();
        msg.setAsset(asset);
        msg.setSymbol(symbol);

        Message message = new Message(
                topic,// topic
                "" + RmqTagEnum.PC_MATCH_ORDER_SNAPSHOT_CREATE.getConstant(),// tag
                "" + RmqTagEnum.PC_MATCH_ORDER_SNAPSHOT_CREATE.getConstant(),// tag
                JsonUtil.toJsonString(msg).getBytes()// body
        );
        safeSend2OrderTopic(message);
        return true;
    }

    private boolean safeSend2OrderTopic(Message message) {
        while (true) {
            try {
                SendResult sr = orderProducer.send(message,
                        (List<MessageQueue> mqs, Message msg, Object arg) -> mqs.get(0),
                        0);
                break;
            } catch (Exception e) {
            }
        }
        return true;
    }


}