package com.hp.sh.expv3.pc.mq.match;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.constant.MqTags;
import com.hp.sh.expv3.pc.module.order.service.PcOrderService;
import com.hp.sh.expv3.pc.module.position.service.PcTradeService;
import com.hp.sh.expv3.pc.mq.match.msg.MatchNotMatchMsg;
import com.hp.sh.expv3.pc.mq.match.msg.MatchedOrderCancelledMsg;
import com.hp.sh.expv3.pc.msg.MatchedMsg;
import com.hp.sh.expv3.pc.msg.PcTradeMsg;
import com.hp.sh.rocketmq.annotation.MQListener;

@Component
@MQListener(orderly=MQListener.ORDERLY_YES)
public class MatchMqConsumer {
	private static final Logger logger = LoggerFactory.getLogger(MatchMqConsumer.class);

	@Autowired
	private PcOrderService pcOrderService;
	
	@Autowired
	private PcTradeService pcTradeService;
	
	@MQListener(tags=MqTags.TAGS_NOT_MATCHED)
	public void handleNotMatch(MatchNotMatchMsg msg){
		this.pcOrderService.setNewStatus(msg.getAccountId(), msg.getAsset(), msg.getSymbol(), msg.getOrderId());
	}
	
	//取消订单
	@MQListener(tags=MqTags.TAGS_CANCELLED)
	public void handleCancelledMsg(MatchedOrderCancelledMsg msg){
		logger.info("收到取消订单消息:{}", msg);
		this.pcOrderService.cancel(msg.getAccountId(), msg.getAsset(), msg.getSymbol(), msg.getOrderId(), msg.getCancelNumber());
	}
	
	//成交
	@MQListener(tags=MqTags.TAGS_PC_TRADE)
	public void handleTradeMsg(PcTradeMsg msg){
		logger.info("收到成交消息:{}", msg);
		pcTradeService.handleTradeOrder(msg);
	}
	
	//撮合成功
	@MQListener(tags=MqTags.TAGS_MATCHED)  
	public void handleMatch(MatchedMsg msg){
		logger.info("收到消息:{}", msg);
		
	}
    
}
