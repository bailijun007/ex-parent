package com.hp.sh.expv3.pc.mq;

import java.math.BigDecimal;
import java.util.List;

import org.apache.rocketmq.client.QueryResult;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.constant.MqTags;
import com.hp.sh.expv3.pc.constant.MqTopic;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.mq.consumer.msg.BookResetMsg;
import com.hp.sh.expv3.pc.mq.consumer.msg.OrderPendingCancelMsg;
import com.hp.sh.expv3.pc.mq.consumer.msg.OrderPendingNewMsg;
import com.hp.sh.expv3.pc.mq.consumer.msg.OrderRebaseMsg;
import com.hp.sh.expv3.pc.mq.consumer.msg.liq.LiqLockMsg;
import com.hp.sh.expv3.utils.BidUtils;

@Component
public class MatchMqSender extends BaseMqSender{
    private static final Logger logger = LoggerFactory.getLogger(MatchMqSender.class);
	
	public MatchMqSender() {
	}
	
	public void sendLiqLockMsg(LiqLockMsg lockMsg){
        super.sendOrderMsg(lockMsg, MqTags.TAGS_POS_LIQ_LOCKED, ""+lockMsg.keys());
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
		msg.setOrderTime(order.getCreated());
		msg.setTimeInForce(order.getTimeInForce());
		this.sendPendingNew(msg);
	}
	
	public void sendPartVolum(PcOrder order){
		//send mq
		OrderPendingNewMsg msg = new OrderPendingNewMsg();
		msg.setAccountId(order.getUserId());
		msg.setAsset(order.getAsset());
		msg.setBidFlag(BidUtils.getBidFlag(order.getCloseFlag(), order.getLongFlag()));
		msg.setCloseFlag(order.getCloseFlag());
		
		BigDecimal remaining = order.getVolume().subtract(order.getFilledVolume());
		msg.setDisplayNumber(remaining);
		msg.setNumber(remaining);
		
		msg.setOrderId(order.getId());
		msg.setPrice(order.getPrice());
		msg.setSymbol(order.getSymbol());
		msg.setOrderType(order.getOrderType());
		msg.setOrderTime(order.getCreated());
		msg.setTimeInForce(order.getTimeInForce());
		this.sendPendingNew(msg);
	}
	
	public void sendPendingNew(OrderPendingNewMsg msg) {
        this.sendOrderMsg(msg, MqTags.TAGS_ORDER_PENDING_NEW, ""+msg.getOrderId());
	}
	
	public void sendPendingCancel(OrderPendingCancelMsg msg) {
	    this.sendOrderMsg(msg, MqTags.TAGS_ORDER_PENDING_CANCEL, ""+msg.getOrderId());
	}

	public void sendBookResetMsg(BookResetMsg msg) {
	    this.sendOrderMsg(msg, MqTags.TAGS_BOOK_RESET, MqTags.TAGS_BOOK_RESET);
	}
	
	public boolean exist(String asset, String symbol, String key, long createdTime) {
		String topic = MqTopic.getOrderTopic(asset, symbol);
		long begin = createdTime-1000*3600;
		long end = createdTime+1000*3600*24*365;
		try {
			QueryResult result = this.producer.queryMessage(topic, key, 1, begin, end);
			List<MessageExt> msgList = result.getMessageList();
			return msgList!=null && msgList.size()>0;
		} catch (MQClientException e) {
			if(e.getResponseCode()==ResponseCode.NO_MESSAGE){
				return false;
			}else{
				throw new RuntimeException(e);
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void sendRebaseMsg(OrderRebaseMsg msg) {
		this.sendOrderMsg(msg, MqTags.TAGS_ORDER_REBASE, MqTags.TAGS_ORDER_REBASE);
	}

}
