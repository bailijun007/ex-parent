package com.hp.sh.expv3.pc.module.order.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.constant.MqTopic;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.order.mq.msg.MatchNotMatchMsg;
import com.hp.sh.expv3.pc.module.order.mq.msg.MatchedOrderCancelledMsg;
import com.hp.sh.expv3.pc.module.order.mq.msg.PcTradeMsg;
import com.hp.sh.expv3.pc.module.order.service.PcOrderService;
import com.hp.sh.rocketmq.annotation.MQListener;

@Component
@MQListener(topic = MqTopic.TOPIC_PCMATCH_BTC__BTC_USD, orderly=MQListener.ORDERLY_YES)
public class MatchMqConsumer {
	private static final Logger logger = LoggerFactory.getLogger(MatchMqConsumer.class);
	
	@Autowired
	private PcOrderService pcOrderService;

	@MQListener(tags=MqTopic.TAGS_NOT_MATCHED)
	public void handleNotMatch(MatchNotMatchMsg msg){
		this.pcOrderService.changeStatus(msg.getAccountId(), msg.getOrderId(), PcOrder.NEW, PcOrder.PENDING_NEW);
		System.out.println(msg);
	}
	
	//取消订单
	@MQListener(tags=MqTopic.TAGS_CANCELLED)
	public void handleMatch(MatchedOrderCancelledMsg msg){
		logger.info("收到消息:{}", msg);
		this.pcOrderService.cancel(msg.getAccountId(), msg.getAsset(), msg.getOrderId(), msg.getCancelNumber());
	}
	
	//成交
	@MQListener(tags=MqTopic.TAGS_PC_TRADE)
	public void handleMatch(PcTradeMsg msg){
		logger.info("收到消息:{}", msg);
		
	}
    
}
