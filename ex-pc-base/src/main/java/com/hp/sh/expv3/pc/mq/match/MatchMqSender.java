package com.hp.sh.expv3.pc.mq.match;

import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.constant.MqTags;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.mq.BaseOrderMsg;
import com.hp.sh.expv3.pc.mq.match.msg.BookResetMsg;
import com.hp.sh.expv3.pc.mq.match.msg.OrderPendingCancelMsg;
import com.hp.sh.expv3.pc.mq.match.msg.OrderPendingNewMsg;
import com.hp.sh.expv3.pc.mq.utils.OrderMessageQueueSelector;
import com.hp.sh.expv3.utils.BidUtils;
import com.hp.sh.rocketmq.codec.MsgCodec;

@Component
public class MatchMqSender {
    private static final Logger logger = LoggerFactory.getLogger(MatchMqSender.class);
	
	@Autowired
    private MQProducer producer;
	
	@Autowired
	private MsgCodec msgCodec;
    
	public MatchMqSender() {
	}
	
	public void sendPendingNew(PcOrder order){
		//send mq
		OrderPendingNewMsg msg = new OrderPendingNewMsg();
		msg.setAccountId(order.getUserId());
		msg.setAsset(order.getAsset());
		msg.setBidFlag(BidUtils.getBidFlag(order.getCloseFlag(), order.getLongFlag()));
		msg.setCloseFlag(order.getCloseFlag());
		msg.setDisplayNumber(order.getVolume());
		msg.setNumber(order.getVolume());
		msg.setOrderId(order.getId());
		msg.setPrice(order.getPrice());
		msg.setSymbol(order.getSymbol());
		msg.setOrderType(order.getOrderType());
		msg.setOrderTime(order.getCreated().getTime());
		msg.setTimeInForce(order.getTimeInForce());
		this.sendPendingNew(msg);
	}
	
	public void sendPendingNew(OrderPendingNewMsg msg) {
		byte[] msgBuff = (byte[]) msgCodec.encode(msg);
		String topic = this.getOrderTopic(msg.getAsset(), msg.getSymbol());
		String tags = MqTags.TAGS_PC_ORDER_PENDING_NEW;
		Message mqMsg = new Message(topic, tags, msgBuff);
		mqMsg.setKeys(""+msg.getOrderId());
        this.send(mqMsg);
	}
	
	public void sendPendingCancel(OrderPendingCancelMsg msg) {
		byte[] msgBuff = (byte[]) msgCodec.encode(msg);
		String topic = this.getOrderTopic(msg.getAsset(), msg.getSymbol());
		String tags = MqTags.TAGS_ORDER_PENDING_CANCEL;
		Message mqMsg = new Message(topic, tags, msgBuff);
		mqMsg.setKeys(""+msg.getOrderId());
	    this.send(mqMsg);
	}

	public void sendBookResetMsg(BookResetMsg msg) {
		byte[] msgBuff = (byte[]) msgCodec.encode(msg);
		String topic = this.getOrderTopic(msg.getAsset(), msg.getSymbol());
		String tags = MqTags.TAGS_PC_BOOK_RESET;
		Message mqMsg = new Message(topic, tags, msgBuff);
		mqMsg.setKeys(tags);
	    this.send(mqMsg);
	}
	
	void sendOrderMsg(BaseOrderMsg msg, String tags, String keys){
		String topic = this.getOrderTopic(msg.getAsset(), msg.getSymbol());
		byte[] msgBuff = (byte[]) msgCodec.encode(msg);
		Message mqMsg = new Message(topic, tags, msgBuff);
		mqMsg.setKeys(keys);
	    this.send(mqMsg);
	}

	private void send(Message mqMsg){
		try {
	        SendResult sendResult = producer.send(mqMsg, new OrderMessageQueueSelector(), 0);
	        System.out.printf("%s%n", sendResult);
	        logger.debug("发送消息:{}", sendResult);
		} catch (Exception e) {
			logger.error("发送消息失败：{}", e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	private String getOrderTopic(String asset, String symbol){
		return "pcOrder_"+asset+"__"+symbol;
	}
    
}
