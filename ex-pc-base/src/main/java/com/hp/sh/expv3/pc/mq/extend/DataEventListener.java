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
import com.hp.sh.expv3.pc.module.position.entity.PcLiqRecord;
import com.hp.sh.expv3.pc.msg.EventMsg;
import com.hp.sh.expv3.pc.msg.EventType;

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
		EventMsg msg = new EventMsg(EventType.PC_ACCOUNT, pcAccountRecord.getId(), pcAccountRecord.getCreated().getTime(), pcAccountRecord.getUserId(), pcAccountRecord.getAsset(), null);
		this.sendEventMsg(msg);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void afterCommit(PcOrderTrade orderTrade) {
		EventMsg orderMsg = new EventMsg(EventType.ORDER, orderTrade.getOrderId(), orderTrade.getCreated().getTime(), orderTrade.getUserId(), orderTrade.getAsset(), orderTrade.getSymbol());
		this.sendEventMsg(orderMsg);
		
		EventMsg posMsg = new EventMsg(EventType.POS, orderTrade.getPosId(), orderTrade.getCreated().getTime(), orderTrade.getUserId(), orderTrade.getAsset(), orderTrade.getSymbol());
		this.sendEventMsg(posMsg);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void afterCommit(PcLiqRecord liqRecord) {
		EventMsg msg = new EventMsg(EventType.ORDER, liqRecord.getPosId(), liqRecord.getCreated().getTime(), liqRecord.getUserId(), liqRecord.getAsset(), liqRecord.getSymbol());
		this.sendEventMsg(msg);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void afterCommit(PcOrder order) {
		EventMsg msg = new EventMsg(EventType.ORDER, order.getId(), order.getCreated().getTime(), order.getUserId(), order.getAsset(), order.getSymbol());
		this.sendEventMsg(msg);
		
		if(order.getClosePosId()!=null && order.getClosePosId()!=0){
			EventMsg msg2 = new EventMsg(EventType.ORDER, order.getClosePosId(), order.getCreated().getTime(), order.getUserId(), order.getAsset(), order.getSymbol());
			this.sendEventMsg(msg2);
		}
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
		channel.toString();
		return channel;
	}

}