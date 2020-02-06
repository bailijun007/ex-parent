package com.hp.sh.expv3.bb.mq.extend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.gitee.hupadev.commons.cache.RedisPublisher;
import com.hp.sh.expv3.bb.constant.EventType;
import com.hp.sh.expv3.bb.module.account.entity.BBAccountRecord;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderTrade;
import com.hp.sh.expv3.bb.mq.extend.msg.BBOrderEvent;
import com.hp.sh.expv3.bb.msg.EventMsg;
import com.hp.sh.expv3.bb.msg.OrderEventMsg;

/**
 * 发送事件消息
 * 
 * @author wangjg
 *
 */
@Component
public class DataEventListener {
	private static final Logger logger = LoggerFactory.getLogger(DataEventListener.class);
	
	@Autowired
	private RedisPublisher publisher;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void afterCommit(BBAccountRecord bBAccountRecord) {
		EventMsg msg = new EventMsg(EventType.PC_ACCOUNT, bBAccountRecord.getId(), bBAccountRecord.getCreated(), bBAccountRecord.getUserId(), bBAccountRecord.getAsset(), null);
		this.sendEventMsg(msg);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void afterCommit(BBOrderTrade orderTrade) {
		OrderEventMsg orderMsg = new OrderEventMsg(EventType.ORDER, orderTrade.getOrderId(), orderTrade.getCreated(), orderTrade.getUserId(), orderTrade.getAsset(), orderTrade.getSymbol());
		orderMsg.setMakerFlag(orderTrade.getMakerFlag());
		orderMsg.setTradeAmt(orderTrade.getVolume());
		orderMsg.setTradeMatchId(""+orderTrade.getMatchTxId());
		orderMsg.setExecId(""+orderTrade.getTxId());
		this.sendEventMsg(orderMsg);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void afterCommit(BBOrderEvent orderEvent) {
		BBOrder order = orderEvent.getPcOrder();
		EventMsg msg = new EventMsg(EventType.ORDER, order.getId(), order.getCreated(), order.getUserId(), order.getAsset(), order.getSymbol());
		this.sendEventMsg(msg);
	}
	
	private void sendEventMsg(EventMsg eventMsg) {
		String channel = this.getChannel(eventMsg);
		logger.debug("publish:{}", channel, eventMsg);
		publisher.publish(channel, eventMsg);
	}
	
	private String getChannel(EventMsg eventMsg){
		String channel = null;
		if(eventMsg.getType()==EventType.PC_ACCOUNT){
			channel = "pc:account:"+eventMsg.getAsset();
		}
		if(eventMsg.getType()==EventType.ORDER){
			channel = "pc:order:"+eventMsg.getAsset()+":"+eventMsg.getSymbol();
		}
		if(eventMsg.getType()==EventType.POS){
			channel = "pc:pos:"+eventMsg.getAsset()+":"+eventMsg.getSymbol();
		}
		if(eventMsg.getType()==EventType.ACCOUNT_SYMBOL){
			channel = "pc:symbol:"+eventMsg.getAsset()+":"+eventMsg.getSymbol();
		}
		channel.toString();
		return channel;
	}

}
