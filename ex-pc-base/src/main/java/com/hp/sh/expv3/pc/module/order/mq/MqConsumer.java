package com.hp.sh.expv3.pc.module.order.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.order.mq.msg.MatchNotMatchMsg;
import com.hp.sh.expv3.pc.module.order.mq.msg.MatchedMsg;
import com.hp.sh.expv3.pc.module.order.service.PcOrderService;
import com.hp.sh.rocketmq.annotation.MQListener;

@Component
public class MqConsumer {
	
	@Autowired
	private PcOrderService pcOrderService;
	
	@MQListener(topic = "pcMatch_BTC__BTC_USD", tags=MqConstant.TAGS_NOT_MATCHED)
	public void handleNotMatch(MatchNotMatchMsg msg){
		this.pcOrderService.changeStatus(msg.getAccountId(), msg.getOrderId(), PcOrder.PENDING_NEW, PcOrder.NEW);
		System.out.println(msg);
	}
	
	@MQListener(topic = "pcMatch_BTC__BTC_USD", tags=MqConstant.TAGS_MATCHED)
	public void handleMatch(MatchedMsg msg){
		
		System.out.println(msg);
	}
    
}
