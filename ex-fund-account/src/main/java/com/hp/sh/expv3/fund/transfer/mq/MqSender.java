package com.hp.sh.expv3.fund.transfer.mq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.json.JsonUtils;

import test.rocketmq.Constant;

@Component
public class MqSender {
    private DefaultMQProducer producer;
    
	public MqSender() {
	    try {
			producer = new DefaultMQProducer(Constant.GROUP);
		    producer.setNamesrvAddr(Constant.ADDR);
			producer.start();
		} catch (MQClientException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void send(Object msg) throws Exception{
		String json = JsonUtils.toJson(msg);
        byte[] msgBuff = json.getBytes(RemotingHelper.DEFAULT_CHARSET);
		Message mqMsg = new Message(Constant.TOPIC, "TagA", msgBuff);
        SendResult sendResult = producer.send(mqMsg);
        System.out.printf("%s%n", sendResult);
	}
    
}
