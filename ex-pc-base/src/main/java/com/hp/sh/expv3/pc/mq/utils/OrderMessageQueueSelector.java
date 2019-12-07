package com.hp.sh.expv3.pc.mq.utils;

import java.util.List;

import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

public class OrderMessageQueueSelector implements MessageQueueSelector {

    @Override
    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
        return mqs.get(0);
    }
}
