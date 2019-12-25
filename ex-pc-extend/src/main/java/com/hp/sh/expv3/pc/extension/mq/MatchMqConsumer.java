package com.hp.sh.expv3.pc.extension.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.msg.MsgConstant;
import com.hp.sh.expv3.pc.msg.PcAccountEvent;
import com.hp.sh.rocketmq.annotation.MQListener;

@Component
@MQListener(topic = MsgConstant.EVENT_TOPIC, orderly=MQListener.ORDERLY_YES)
public class MatchMqConsumer {
	private static final Logger logger = LoggerFactory.getLogger(MatchMqConsumer.class);

	public void handleMatch(PcAccountEvent msg){
		logger.info("收到消息:{}", msg);
		
	}
	
}
