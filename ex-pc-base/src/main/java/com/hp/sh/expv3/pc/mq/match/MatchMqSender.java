package com.hp.sh.expv3.pc.mq.match;

import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.constant.MqTags;
import com.hp.sh.expv3.pc.mq.BaseOrderMsg;
import com.hp.sh.expv3.pc.mq.match.msg.BookResetMsg;
import com.hp.sh.expv3.pc.mq.match.msg.OrderPendingCancelMsg;
import com.hp.sh.expv3.pc.mq.match.msg.OrderPendingNewMsg;
import com.hp.sh.expv3.pc.mq.utils.OrderMessageQueueSelector;
import com.hp.sh.rocketmq.codec.MsgCodec;

@Component
public class MatchMqSender {
	
	@Autowired
    private MQProducer producer;
	
	@Autowired
	private MsgCodec msgCodec;
    
	public MatchMqSender() {
	}
	
	public void sendPendingNew(OrderPendingNewMsg msg) throws Exception{
		byte[] msgBuff = (byte[]) msgCodec.encode(msg);
		String topic = this.getOrderTopic(msg.getAsset(), msg.getSymbol());
		String tags = MqTags.TAGS_PC_ORDER_PENDING_NEW;
		Message mqMsg = new Message(topic, tags, msgBuff);
		mqMsg.setKeys(""+msg.getOrderId());
        this.send(mqMsg);
	}
	
	public void sendPendingCancel(OrderPendingCancelMsg msg) throws Exception {
		byte[] msgBuff = (byte[]) msgCodec.encode(msg);
		String topic = this.getOrderTopic(msg.getAsset(), msg.getSymbol());
		String tags = MqTags.TAGS_ORDER_PENDING_CANCEL;
		Message mqMsg = new Message(topic, tags, msgBuff);
		mqMsg.setKeys(""+msg.getOrderId());
	    this.send(mqMsg);
	}

	public void sendBookResetMsg(BookResetMsg msg) throws Exception {
		byte[] msgBuff = (byte[]) msgCodec.encode(msg);
		String topic = this.getOrderTopic(msg.getAsset(), msg.getSymbol());
		String tags = MqTags.TAGS_PC_BOOK_RESET;
		Message mqMsg = new Message(topic, tags, msgBuff);
		mqMsg.setKeys(tags);
	    this.send(mqMsg);
	}
	
	void sendOrderMsg(BaseOrderMsg msg, String tags, String keys) throws Exception{
		String topic = this.getOrderTopic(msg.getAsset(), msg.getSymbol());
		byte[] msgBuff = (byte[]) msgCodec.encode(msg);
		Message mqMsg = new Message(topic, tags, msgBuff);
		mqMsg.setKeys(keys);
	    this.send(mqMsg);
	}

	private void send(Message mqMsg) throws Exception{
        SendResult sendResult = producer.send(mqMsg, new OrderMessageQueueSelector(), 0);
        System.out.printf("%s%n", sendResult);
	}

	private String getOrderTopic(String asset, String symbol){
		return "pcOrder_"+asset+"__"+symbol;
	}
    
}
