package com.hp.sh.expv3.fund.transfer.mq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.json.JsonUtils;
import com.hp.sh.expv3.fund.transfer.constant.MQConstant;
import com.hp.sh.expv3.fund.transfer.mq.msg.NewTransfer;

@Component
public class MqSender {
	private static final Logger logger = LoggerFactory.getLogger(MqSender.class);
	
	@Value("${hp.rocketmq.namesrvAddr}")
	private String namesrvAddr;
	
	@Value("${hp.rocketmq.namespace}")
	private String namespace;
	
    private DefaultMQProducer producer;
    
	public MqSender() {
	}
	
	public void sendMsg(NewTransfer msg){
		try{
			String json = JsonUtils.toJson(msg);
	        byte[] msgBuff = json.getBytes(RemotingHelper.DEFAULT_CHARSET);
			Message mqMsg = new Message(MQConstant.TOPIC_TRANSFER, msg.getClass().getSimpleName(), msgBuff);
	        SendResult sendResult = producer().send(mqMsg);
	        System.out.printf("%s%n", sendResult);
		}catch(Exception e){
			logger.error("发送消息失败：", e);
		}
	}

	private DefaultMQProducer producer() {
		if(this.producer==null){
		    try {
				producer = new DefaultMQProducer();
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
