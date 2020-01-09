/**
 * @author 10086
 * @date 2019/10/21
 */
package com.hp.sh.expv3.match.component.notify;

import com.hp.sh.expv3.match.component.rocketmq.BaseBbOrderProducer;
import com.hp.sh.expv3.match.config.setting.BbmatchRocketMqSetting;
import com.hp.sh.expv3.match.enums.RmqTagEnum;
import com.hp.sh.expv3.match.mqmsg.BbMatchOrderRebaseMqMsgDto;
import com.hp.sh.expv3.match.mqmsg.BbMatchOrderSnapshotMqMsgDto;
import com.hp.sh.expv3.match.util.BbRocketMqUtil;
import com.hp.sh.expv3.match.util.JsonUtil;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
public class BbOrderMqNotify {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BaseBbOrderProducer orderProducer;

    @Autowired
    private BbmatchRocketMqSetting bbmatchRocketMqSetting;

    public boolean sendOrderSnapshotTrigger(String asset, String symbol) {
        String topic = BbRocketMqUtil.buildBbOrderTopicName(bbmatchRocketMqSetting.getBbOrderTopicNamePattern(), asset, symbol);

        BbMatchOrderSnapshotMqMsgDto msg = new BbMatchOrderSnapshotMqMsgDto();
        msg.setAsset(asset);
        msg.setSymbol(symbol);

        Message message = buildMessage(
                topic,// topic
                "" + RmqTagEnum.MATCH_ORDER_SNAPSHOT_CREATE.getConstant(),// tag
                "" + RmqTagEnum.MATCH_ORDER_SNAPSHOT_CREATE.getConstant(),// tag
                msg// body
        );
        safeSend2OrderTopic(message);

        if (logger.isDebugEnabled()) {
            logger.debug("{} {} topic:{} tag:{},keys:{} {}", asset, symbol, message.getTopic(), message.getTags(), message.getKeys(), JsonUtil.toJsonString(msg));
        }
        return true;
    }

    public boolean sendMatchStart(String asset, String symbol) {
        String topic = BbRocketMqUtil.buildBbOrderTopicName(bbmatchRocketMqSetting.getBbOrderTopicNamePattern(), asset, symbol);

        BbMatchOrderSnapshotMqMsgDto msg = new BbMatchOrderSnapshotMqMsgDto();
        msg.setAsset(asset);
        msg.setSymbol(symbol);

        Message message = buildMessage(
                topic,// topic
                "" + RmqTagEnum.MATCH_CONSUMER_START.getConstant(),// tag
                "" + RmqTagEnum.MATCH_CONSUMER_START.getConstant(),// keys
                msg// body
        );
        safeSend2OrderTopic(message);
        if (logger.isDebugEnabled()) {
            logger.debug("{} {} topic:{} tag:{},keys:{} {}", asset, symbol, message.getTopic(), message.getTags(), message.getKeys(), JsonUtil.toJsonString(msg));
        }
        return true;
    }

    public boolean sendBbOrderRebase(String asset, String symbol) {
        String topic = BbRocketMqUtil.buildBbOrderTopicName(bbmatchRocketMqSetting.getBbOrderTopicNamePattern(), asset, symbol);

        BbMatchOrderRebaseMqMsgDto msg = new BbMatchOrderRebaseMqMsgDto();
        msg.setAsset(asset);
        msg.setSymbol(symbol);

        Message message = buildMessage(
                topic,// topic
                "" + RmqTagEnum.ORDER_REBASE.getConstant(),// tag
                "" + RmqTagEnum.ORDER_REBASE.getConstant(),// keys
                msg// body
        );
        safeSend2OrderTopic(message);
        if (logger.isDebugEnabled()) {
            logger.debug("{} {} topic:{} tag:{},keys:{} {}", asset, symbol, message.getTopic(), message.getTags(), message.getKeys(), JsonUtil.toJsonString(msg));
        }
        return true;
    }

    private Message buildMessage(String topic, String tags, String keys, Object o) {
        try {
            return new Message(
                    topic,// topic
                    tags,// tag
                    keys, // pos id
                    JsonUtil.toJsonString(o).getBytes(RemotingHelper.DEFAULT_CHARSET)// body
            );
        } catch (UnsupportedEncodingException e) {
        }
        return null;
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