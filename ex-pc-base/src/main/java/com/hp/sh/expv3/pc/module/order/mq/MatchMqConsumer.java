package com.hp.sh.expv3.pc.module.order.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.order.mq.msg.MatchNotMatchMsg;
import com.hp.sh.expv3.pc.module.order.mq.msg.MatchedMsg;
import com.hp.sh.expv3.pc.module.order.mq.msg.MatchedOrderCancelledMsg;
import com.hp.sh.expv3.pc.module.order.mq.utils.PcMatchMqConstant;
import com.hp.sh.expv3.pc.module.order.service.PcOrderService;
import com.hp.sh.rocketmq.annotation.MQListener;

@Component
public class MatchMqConsumer {
	
	@Autowired
	private PcOrderService pcOrderService;
	
	@MQListener(topic = PcMatchMqConstant.PCMATCH_BTC__BTC_USD, tags=PcMatchMqConstant.TAGS_NOT_MATCHED)
	public void handleNotMatch(MatchNotMatchMsg msg){
		this.pcOrderService.changeStatus(msg.getAccountId(), msg.getOrderId(), PcOrder.NEW, PcOrder.PENDING_NEW);
		System.out.println(msg);
	}
	
	//取消订单
	@MQListener(topic = PcMatchMqConstant.PCMATCH_BTC__BTC_USD, tags=PcMatchMqConstant.TAGS_CANCELLED)
	public void handleMatch(MatchedOrderCancelledMsg msg){
		this.pcOrderService.cancel(msg.getAccountId(), msg.getAsset(), msg.getOrderId(), msg.getCancelNumber());
		
		System.out.println(msg);
	}
	
	@MQListener(topic = PcMatchMqConstant.PCMATCH_BTC__BTC_USD, tags=PcMatchMqConstant.TAGS_MATCHED)
	public void handleMatch(MatchedMsg msg){
		
		System.out.println(msg);
	}
    
}
