package com.hp.sh.expv3.pc.module.order.mq;

import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.module.order.mq.msg.BookResetMsg;
import com.hp.sh.expv3.pc.module.order.mq.msg.OrderPendingCancelMsg;
import com.hp.sh.expv3.pc.module.order.mq.msg.OrderPendingNewMsg;
import com.hp.sh.expv3.pc.module.order.mq.utils.OrderMessageQueueSelector;
import com.hp.sh.expv3.pc.module.order.mq.utils.PcMatchMqConstant;
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
		String tags = PcMatchMqConstant.TAGS_PC_ORDER_PENDING_NEW;
		Message mqMsg = new Message(topic, tags, msgBuff);
		mqMsg.setKeys(""+msg.getOrderId());
        this.send(mqMsg);
	}
	
	public void sendPendingCancel(OrderPendingCancelMsg msg) throws Exception {
		byte[] msgBuff = (byte[]) msgCodec.encode(msg);
		String topic = this.getOrderTopic(msg.getAsset(), msg.getSymbol());
		String tags = PcMatchMqConstant.TAGS_ORDER_PENDING_CANCEL;
		Message mqMsg = new Message(topic, tags, msgBuff);
		mqMsg.setKeys(""+msg.getOrderId());
	    this.send(mqMsg);
	}

	public void sendBookResetMsg(BookResetMsg msg) throws Exception {
		byte[] msgBuff = (byte[]) msgCodec.encode(msg);
		String topic = this.getOrderTopic(msg.getAsset(), msg.getSymbol());
		String tags = PcMatchMqConstant.TAGS_PC_BOOK_RESET;
		Message mqMsg = new Message(topic, tags, msgBuff);
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
