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
import com.hp.sh.expv3.match.util.JsonUtil;
import com.hp.sh.expv3.match.util.BbRocketMqUtil;
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
                "" + RmqTagEnum.BB_MATCH_ORDER_SNAPSHOT_CREATE.getConstant(),// tag
                "" + RmqTagEnum.BB_MATCH_ORDER_SNAPSHOT_CREATE.getConstant(),// tag
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
                "" + RmqTagEnum.BB_MATCH_CONSUMER_START.getConstant(),// tag
                "" + RmqTagEnum.BB_MATCH_CONSUMER_START.getConstant(),// keys
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
                "" + RmqTagEnum.BB_ORDER_REBASE.getConstant(),// tag
                "" + RmqTagEnum.BB_ORDER_REBASE.getConstant(),// keys
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
        boolean first = true;
        while (true) {
            try {
                orderProducer.send(message,
                        (List<MessageQueue> mqs, Message msg, Object arg) -> mqs.get(0),
                        0);
                first = false;
                break;
            } catch (Exception e) {
                if (first) {
                    logger.error(e.getMessage(), e);
                } else {
                    logger.error(e.getMessage());
                }
            }
            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}