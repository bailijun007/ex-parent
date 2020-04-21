package com.hp.sh.expv3.bb.mq.listen.mq;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.mybatis.ex.UpdateException;
import com.hp.sh.expv3.bb.constant.MqTags;
import com.hp.sh.expv3.bb.module.fail.service.BBMqMsgService;
import com.hp.sh.expv3.bb.module.order.service.BBOrderService;
import com.hp.sh.expv3.bb.module.order.service.BBTradeService;
import com.hp.sh.expv3.bb.mq.msg.in.BBMatchNotMatchMsg;
import com.hp.sh.expv3.bb.mq.msg.in.BbOrderCancelMqMsg;
import com.hp.sh.expv3.bb.strategy.vo.BBTradeVo;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.rocketmq.annotation.MQListener;

@Component
@MQListener(orderly=MQListener.ORDERLY_YES)
public class MatchMqConsumer {
	private static final Logger logger = LoggerFactory.getLogger(MatchMqConsumer.class);

	@Autowired
	private BBOrderService orderService;
	
	@Autowired
	private BBTradeService tradeService;

	@Autowired
	private BBMqMsgService msgService;

	//撮合未成交
	@MQListener(tags=MqTags.TAGS_NOT_MATCHED)
	public void handleNotMatch(BBMatchNotMatchMsg msg){
		logger.info("收到撮合未成交消息:{}", msg);
		orderService.setNewStatus(msg.getAccountId(), msg.getAsset(), msg.getSymbol(), msg.getOrderId());
	}
	
	//取消订单
	@MQListener(tags=MqTags.TAGS_CANCELLED)
	public void handleCancelledMsg(BbOrderCancelMqMsg msg){
		logger.info("收到取消订单消息:{}", msg);
		try{
			boolean existTade = this.msgService.exist(msg.getAccountId(), MqTags.TAGS_TRADE, ""+msg.getOrderId());
			if(!existTade){
				orderService.setCancelled(msg.getAccountId(), msg.getAsset(), msg.getSymbol(), msg.getOrderId());
			}else{
				msgService.saveIfNotExists(MqTags.TAGS_CANCELLED, msg, "存在未处理的trade");
			}
		}catch(UpdateException e){
			throw e;
		}catch(Exception e){
			Throwable cause = ExceptionUtils.getRootCause(e);
			if(cause instanceof UpdateException){
				throw (UpdateException)cause;
			}
			logger.error(e.getMessage(), e);
			msgService.saveIfNotExists(MqTags.TAGS_CANCELLED, msg, e.getMessage());
		}
	}
	
	//成交
	@MQListener(tags=MqTags.TAGS_TRADE)
	public void handleTradeMsg(BBTradeVo msg){
		logger.info("收到用户成交消息:{}", msg);
		try{
			this.tradeService.handleTrade(msg);
		}catch(UpdateException e){
			throw e;
		}catch(Exception e){
			Throwable cause = ExceptionUtils.getRootCause(e);
			if(cause instanceof UpdateException){
				throw (UpdateException)cause;
			}
			logger.error(e.getMessage(), e);
			msgService.save(MqTags.TAGS_TRADE, msg, e.getMessage());
		}
	}
    
}
