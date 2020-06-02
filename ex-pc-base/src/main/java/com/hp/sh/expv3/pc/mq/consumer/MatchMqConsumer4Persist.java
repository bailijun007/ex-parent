package com.hp.sh.expv3.pc.mq.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.constant.MqTags;
import com.hp.sh.expv3.pc.job.msghandler.PcMsgHandleThreadJobConfig;
import com.hp.sh.expv3.pc.module.msg.entity.PcMessageExt;
import com.hp.sh.expv3.pc.module.msg.service.PcMessageExtService;
import com.hp.sh.expv3.pc.mq.consumer.msg.PcCancelledMsg;
import com.hp.sh.expv3.pc.mq.consumer.msg.PcNotMatchedMsg;
import com.hp.sh.expv3.pc.mq.consumer.msg.PcTradeMsg;
import com.hp.sh.expv3.pc.mq.consumer.msg.liq.LiqCancelledMsg;
import com.hp.sh.rocketmq.annotation.MQListener;

@Component
@MQListener(orderly=MQListener.ORDERLY_YES)
@ConditionalOnProperty(name="mq.orderly.consumer.select", havingValue="2")
public class MatchMqConsumer4Persist {
	private static final Logger logger = LoggerFactory.getLogger(MatchMqConsumer4Persist.class);


	@Autowired
	private PcMessageExtService msgService;
	
	@Autowired
	private PcMsgHandleThreadJobConfig msgHandleThreadJob;

	public MatchMqConsumer4Persist() {
		logger.info("init");
	}

	//撮合未成交
	@MQListener(tags=MqTags.TAGS_NOT_MATCHED)
	public void handleNotMatch(PcNotMatchedMsg msg){
		logger.info("收到撮合未成交消息:{}", msg);
		try{
			PcMessageExt entity = msgService.saveNotMatchedMsg(MqTags.TAGS_NOT_MATCHED, msg);
			this.triggerShardThread(entity.getShardId());
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
	public void handleCancelledMsg(PcCancelledMsg msg){
		logger.info("收到取消订单消息:{}", msg);
		PcMessageExt entity = msgService.saveCancelIfNotExists(MqTags.TAGS_CANCELLED, msg, null);
		if(entity!=null){
			this.triggerShardThread(entity.getShardId());
		}else{
			logger.warn("订单取消msg已存在："+msg.getOrderId());
		}
	}
	
	//成交
	@MQListener(tags=MqTags.TAGS_TRADE)
	public void handleTradeMsg(PcTradeMsg msg){
		logger.info("收到用户成交消息:{}", msg);
		try{
			PcMessageExt entity = msgService.saveTradeMsg(MqTags.TAGS_TRADE, msg, null);
			this.triggerShardThread(entity.getShardId());
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
	
	//强平取消
	@MQListener(tags = MqTags.TAGS_ORDER_ALL_CANCELLED)
	public void handleLiqCancelledMsg(LiqCancelledMsg msg){
		logger.warn("收到强平撤销消息:{}", msg);
		try{
			PcMessageExt entity = msgService.saveLiqCancel(MqTags.TAGS_ORDER_ALL_CANCELLED, msg, null);
			this.triggerShardThread(entity.getShardId());
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
	
	private void triggerShardThread(Long shardId){
		msgHandleThreadJob.trigger(shardId);
	}
    
}
