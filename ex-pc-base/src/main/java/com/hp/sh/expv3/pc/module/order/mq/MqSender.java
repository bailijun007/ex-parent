package com.hp.sh.expv3.pc.module.order.mq;

import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.module.order.mq.msg.OrderPendingNewMsg;
import com.hp.sh.rocketmq.codec.MsgCodec;
import com.hp.sh.rocketmq.msg.RocketAttrMsg;

@Component
public class MqSender {
	
	@Autowired
    private MQProducer producer;
	
	@Autowired
	private MsgCodec msgCodec;
    
	public MqSender() {
	}
	
	public void send(RocketAttrMsg msg) throws Exception{
		byte[] msgBuff = (byte[]) msgCodec.encode(msg);
		Message mqMsg = new Message(msg.getTopic(), msg.getTags(), msg.getKeys(), msgBuff);
		this.send(mqMsg);
	}
	
	public void send(OrderPendingNewMsg msg) throws Exception{
		byte[] msgBuff = (byte[]) msgCodec.encode(msg);
		Message mqMsg = new Message(msg.topic(), msg.tags(), msgBuff);
		mqMsg.setKeys(""+msg.getOrderId());
        this.send(mqMsg);
	}
	
	public void send(Message mqMsg) throws Exception{
        SendResult sendResult = producer.send(mqMsg, new OrderMessageQueueSelector(), 0);
        System.out.printf("%s%n", sendResult);
	}
    
}
