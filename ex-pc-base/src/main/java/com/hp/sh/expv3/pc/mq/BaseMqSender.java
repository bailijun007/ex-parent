package com.hp.sh.expv3.pc.mq;

import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;

import com.hp.sh.expv3.pc.mq.utils.OrderMessageQueueSelector;
import com.hp.sh.rocketmq.codec.MsgCodec;

public class BaseMqSender {
	
	@Autowired
    private MQProducer producer;
	
	@Autowired
	private MsgCodec msgCodec;
    
	public BaseMqSender() {
	}
	
	protected void sendOrderMsg(BaseOrderMsg msg, String tags, String keys){
		String topic = this.getOrderTopic(msg.getAsset(), msg.getSymbol());
		byte[] msgBuff = (byte[]) msgCodec.encode(msg);
		Message mqMsg = new Message(topic, tags, msgBuff);
		mqMsg.setKeys(keys);
	    this.send(mqMsg);
	}

	protected void send(Message mqMsg) {
		try {
	        SendResult sendResult = producer.send(mqMsg, new OrderMessageQueueSelector(), 0);
	        System.out.printf("%s%n", sendResult);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String getOrderTopic(String asset, String symbol){
		return "pcOrder_"+asset+"__"+symbol;
	}
    
}
