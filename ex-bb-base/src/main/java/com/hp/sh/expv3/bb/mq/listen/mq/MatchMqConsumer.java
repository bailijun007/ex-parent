package com.hp.sh.expv3.bb.mq.listen.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.base.exceptions.CommonError;
import com.gitee.hupadev.base.exceptions.ExceptionUtils;
import com.gitee.hupadev.commons.mybatis.ex.UpdateException;
import com.hp.sh.expv3.bb.constant.MqTags;
import com.hp.sh.expv3.bb.module.fail.service.BBMqMsgService;
import com.hp.sh.expv3.bb.module.order.service.BBOrderService;
import com.hp.sh.expv3.bb.module.order.service.BBTradeService;
import com.hp.sh.expv3.bb.mq.msg.in.BBCancelledMsg;
import com.hp.sh.expv3.bb.mq.msg.in.BBNotMatchMsg;
import com.hp.sh.expv3.bb.strategy.vo.BBTradeVo;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.rocketmq.annotation.MQListener;

//@Component
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
	public void handleNotMatch(BBNotMatchMsg msg){
		logger.info("收到撮合未成交消息:{}", msg);
		orderService.setNewStatus(msg.getAccountId(), msg.getAsset(), msg.getSymbol(), msg.getOrderId());
	}
	
	//取消订单
	@MQListener(tags=MqTags.TAGS_CANCELLED)
	public void handleCancelledMsg(BBCancelledMsg msg){
		logger.info("收到取消订单消息:{}", msg);
		try{
			boolean existTade = this.msgService.exist(msg.getAccountId(), MqTags.TAGS_TRADE, ""+msg.getOrderId());
			if(!existTade){
				orderService.setCancelled(msg.getAccountId(), msg.getAsset(), msg.getSymbol(), msg.getOrderId());
			}else{
				msgService.saveIfNotExists(MqTags.TAGS_CANCELLED, msg, "存在未处理的trade");
			}
		}catch(Exception e){
			this.checkException(e);
			logger.error("消息处理异常：msg={}, ex={}", e.getMessage(), e.toString(), e);
			msgService.saveIfNotExists(MqTags.TAGS_CANCELLED, msg, e.getMessage());
		}
	}
	
	//成交
	@MQListener(tags=MqTags.TAGS_TRADE)
	public void handleTradeMsg(BBTradeVo msg){
		logger.info("收到用户成交消息:{}", msg);
		try{
			this.tradeService.handleTrade(msg);
		}catch(Exception e){
			this.checkException(e);
			logger.error("消息处理异常：msg={}, ex={}",e.getMessage() , e.toString(), e);
			msgService.save(MqTags.TAGS_TRADE, msg, e.getMessage());
		}
	}
	
	private void checkException(Exception e){
		if(isResendException(e)){
			throw (RuntimeException)e;
		}
	}
	
	public static boolean isResendException(Exception e){
		Throwable cause = ExceptionUtils.getCause(e);
		if(cause instanceof UpdateException){
			return true;
		}
		if(cause instanceof DataAccessException){
			return true;
		}
		if(cause instanceof ExException){
			ExException ex = (ExException)cause;
			if(ex.getCode()==CommonError.LOCK.getCode()){
				return true;
			}
			if(ex.getCode()==CommonError.DATA_EXPIRED.getCode()){
				return true;
			}
		}
		return false;
	}
    
}
