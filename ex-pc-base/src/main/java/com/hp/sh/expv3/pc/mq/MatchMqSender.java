package com.hp.sh.expv3.pc.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.constant.MqTags;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.mq.liq.msg.LiqLockMsg;
import com.hp.sh.expv3.pc.mq.match.msg.BookResetMsg;
import com.hp.sh.expv3.pc.mq.match.msg.OrderPendingCancelMsg;
import com.hp.sh.expv3.pc.mq.match.msg.OrderPendingNewMsg;
import com.hp.sh.expv3.utils.BidUtils;

@Component
public class MatchMqSender extends BaseMqSender{
    private static final Logger logger = LoggerFactory.getLogger(MatchMqSender.class);
	
	public MatchMqSender() {
	}
	
	public void sendLiqLockMsg(LiqLockMsg lockMsg){
        super.sendOrderMsg(lockMsg, MqTags.TAGS_PC_POS_LIQ_LOCKED, ""+lockMsg.keys());
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
	
	public void sendPendingNew(OrderPendingNewMsg msg) {
        this.sendOrderMsg(msg, MqTags.TAGS_PC_ORDER_PENDING_NEW, ""+msg.getOrderId());
	}
	
	public void sendPendingCancel(OrderPendingCancelMsg msg) {
	    this.sendOrderMsg(msg, MqTags.TAGS_ORDER_PENDING_CANCEL, ""+msg.getOrderId());
	}

	public void sendBookResetMsg(BookResetMsg msg) {
	    this.sendOrderMsg(msg, MqTags.TAGS_PC_BOOK_RESET, null);
	}

}