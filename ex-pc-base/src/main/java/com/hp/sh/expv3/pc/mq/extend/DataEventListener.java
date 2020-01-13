package com.hp.sh.expv3.pc.mq.extend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.gitee.hupadev.commons.cache.RedisPublisher;
import com.hp.sh.expv3.pc.module.account.entity.PcAccountRecord;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.order.entity.PcOrderTrade;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.symbol.entity.PcAccountSymbol;
import com.hp.sh.expv3.pc.mq.extend.msg.PcOrderEvent;
import com.hp.sh.expv3.pc.msg.EventMsg;
import com.hp.sh.expv3.pc.msg.EventType;
import com.hp.sh.expv3.pc.msg.OrderEventMsg;

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
	public void afterCommit(PcAccountRecord pcAccountRecord) {
		EventMsg msg = new EventMsg(EventType.PC_ACCOUNT, pcAccountRecord.getId(), pcAccountRecord.getCreated(), pcAccountRecord.getUserId(), pcAccountRecord.getAsset(), null);
		this.sendEventMsg(msg);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void afterCommit(PcOrderTrade orderTrade) {
		OrderEventMsg orderMsg = new OrderEventMsg(EventType.ORDER, orderTrade.getOrderId(), orderTrade.getCreated(), orderTrade.getUserId(), orderTrade.getAsset(), orderTrade.getSymbol());
		orderMsg.setMakerFlag(orderTrade.getMakerFlag());
		orderMsg.setTradeAmt(orderTrade.getVolume());
		orderMsg.setTradeMatchId(""+orderTrade.getMatchTxId());
		orderMsg.setExecId(""+orderTrade.getTxId());
		this.sendEventMsg(orderMsg);
		
		EventMsg posMsg = new EventMsg(EventType.POS, orderTrade.getPosId(), orderTrade.getCreated(), orderTrade.getUserId(), orderTrade.getAsset(), orderTrade.getSymbol());
		this.sendEventMsg(posMsg);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void afterCommit(PcPosition pcPosition) {
		EventMsg msg = new EventMsg(EventType.POS, pcPosition.getId(), pcPosition.getCreated(), pcPosition.getUserId(), pcPosition.getAsset(), pcPosition.getSymbol());
		this.sendEventMsg(msg);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void afterCommit(PcOrderEvent orderEvent) {
		PcOrder order = orderEvent.getPcOrder();
		EventMsg msg = new EventMsg(EventType.ORDER, order.getId(), order.getCreated(), order.getUserId(), order.getAsset(), order.getSymbol());
		this.sendEventMsg(msg);
		
		if(order.getClosePosId()!=null && order.getClosePosId()!=0){
			EventMsg msg2 = new EventMsg(EventType.POS, order.getClosePosId(), order.getCreated(), order.getUserId(), order.getAsset(), order.getSymbol());
			this.sendEventMsg(msg2);
		}
	}
	
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void afterCommit(PcAccountSymbol accountSymbol) {
		EventMsg msg = new EventMsg(EventType.ACCOUNT_SYMBOL, accountSymbol.getId(), accountSymbol.getModified(), accountSymbol.getUserId(), accountSymbol.getAsset(), accountSymbol.getSymbol());
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
