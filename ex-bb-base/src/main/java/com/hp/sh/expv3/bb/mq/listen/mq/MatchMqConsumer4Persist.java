package com.hp.sh.expv3.bb.mq.listen.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.constant.MqTags;
import com.hp.sh.expv3.bb.job.BBMsgHandleThreadJobConfig;
import com.hp.sh.expv3.bb.module.msg.entity.BBMessageExt;
import com.hp.sh.expv3.bb.module.msg.service.BBMessageExtService;
import com.hp.sh.expv3.bb.module.order.service.BBOrderService;
import com.hp.sh.expv3.bb.module.order.service.BBTradeService;
import com.hp.sh.expv3.bb.mq.msg.in.BBCancelledMsg;
import com.hp.sh.expv3.bb.mq.msg.in.BBNotMatchMsg;
import com.hp.sh.expv3.bb.mq.msg.in.BBTradeMsg;
import com.hp.sh.rocketmq.annotation.MQListener;

@Component
@MQListener(orderly=MQListener.ORDERLY_YES)
@ConditionalOnProperty(name="mq.orderly.consumer.select", havingValue="2")
public class MatchMqConsumer4Persist {
	private static final Logger logger = LoggerFactory.getLogger(MatchMqConsumer4Persist.class);

	@Autowired
	private BBOrderService orderService;
	
	@Autowired
	private BBTradeService tradeService;

	@Autowired
	private BBMessageExtService msgService;
	
	@Autowired
	private BBMsgHandleThreadJobConfig msgHandleThreadJob;

	public MatchMqConsumer4Persist() {
		super();
		logger.info("init");
	}

	//撮合未成交
	@MQListener(tags=MqTags.TAGS_NOT_MATCHED)
	public void handleNotMatch(BBNotMatchMsg msg){
		logger.info("收到撮合未成交消息:{}", msg);
		try{
			BBMessageExt entity = msgService.saveNotMatchedMsg(MqTags.TAGS_NOT_MATCHED, msg);
			msgHandleThreadJob.trigger(entity.getShardId());
		}catch(Exception e){
			String s = e.getMessage();
			if(s!=null && s.contains("Duplicate entry")){
				if(msgService.existMsgId(msg.getAccountId(), msg.getMsgId())){
					return;
				}
			}
			throw e;
		}
	}
	
	//取消订单
	@MQListener(tags=MqTags.TAGS_CANCELLED)
	public void handleCancelledMsg(BBCancelledMsg msg){
		logger.info("收到取消订单消息:{}", msg);
		BBMessageExt entity = msgService.saveCancelIfNotExists(MqTags.TAGS_CANCELLED, msg, null);
		if(entity!=null){
			msgHandleThreadJob.trigger(entity.getShardId());
		}else{
			logger.warn("订单取消msg已存在："+msg.getOrderId());
		}
	}
	
	//成交
	@MQListener(tags=MqTags.TAGS_TRADE)
	public void handleTradeMsg(BBTradeMsg msg){
		logger.info("收到用户成交消息:{}", msg);
		try{
			BBMessageExt entity = msgService.saveTradeMsg(MqTags.TAGS_TRADE, msg, null);
			msgHandleThreadJob.trigger(entity.getShardId());
		}catch(Exception e){
			String s = e.getMessage();
			if(s!=null && s.contains("Duplicate entry")){
				if(msgService.existMsgId(msg.getAccountId(), msg.getMsgId())){
					return;
				}
			}
			throw e;
		}
	}
    
}
