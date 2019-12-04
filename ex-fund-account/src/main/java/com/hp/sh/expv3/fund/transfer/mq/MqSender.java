package com.hp.sh.expv3.fund.transfer.mq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.json.JsonUtils;
import com.hp.sh.expv3.fund.transfer.constant.MQConstant;

@Component
public class MqSender {
	
	@Value("${rocketmq.default.namesrvAddr}")
	private String namesrvAddr;
	
	@Value("${rocketmq.default.namespace}")
	private String namespace;
	
    private DefaultMQProducer producer;
    
	public MqSender() {
	}
	
	public void send(Object msg) throws Exception{
		String json = JsonUtils.toJson(msg);
        byte[] msgBuff = json.getBytes(RemotingHelper.DEFAULT_CHARSET);
		Message mqMsg = new Message(MQConstant.TOPIC, msg.getClass().getSimpleName(), msgBuff);
        SendResult sendResult = producer().send(mqMsg);
        System.out.printf("%s%n", sendResult);
	}

	private DefaultMQProducer producer() {
		if(this.producer==null){
		    try {
				producer = new DefaultMQProducer(MQConstant.GROUP);
			    producer.setNamesrvAddr(namesrvAddr);
			    producer.setNamespace(namespace);
				producer.start();
			} catch (MQClientException e) {
				throw new RuntimeException(e);
			}
		}
		return producer;
	}
    
}
