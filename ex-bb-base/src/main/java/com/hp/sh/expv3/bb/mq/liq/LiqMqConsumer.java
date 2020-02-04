package com.hp.sh.expv3.bb.mq.liq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.constant.MqTags;
import com.hp.sh.expv3.bb.module.position.service.BBLiqService;
import com.hp.sh.expv3.bb.mq.liq.msg.LiqCancelledMsg;
import com.hp.sh.rocketmq.annotation.MQListener;

@Component
@MQListener(orderly=MQListener.ORDERLY_YES)
public class LiqMqConsumer {
	private static final Logger logger = LoggerFactory.getLogger(LiqMqConsumer.class);
    
    @Autowired
    private BBLiqService bBLiqService;

	@MQListener(tags = MqTags.TAGS_ORDER_ALL_CANCELLED)
	public void handleCancelled(LiqCancelledMsg msg){
		logger.warn("收到强平撤销消息:{}", msg);
		bBLiqService.cancelCloseOrder(msg.getAccountId(), msg.getAsset(), msg.getSymbol(), msg.getLongFlag(), msg.getPosId(), msg.getCancelOrders(), msg.getLastFlag());
	}
    
}
