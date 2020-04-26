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
import com.hp.sh.expv3.bb.job.BBMsgHandleThreadJob;
import com.hp.sh.expv3.bb.module.msg.service.BBMessageExtService;
import com.hp.sh.expv3.bb.module.order.service.BBOrderService;
import com.hp.sh.expv3.bb.module.order.service.BBTradeService;
import com.hp.sh.expv3.bb.mq.msg.in.BBCancelledMsg;
import com.hp.sh.expv3.bb.mq.msg.in.BBNotMatchMsg;
import com.hp.sh.expv3.bb.strategy.vo.BBTradeVo;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.rocketmq.annotation.MQListener;

@Component
@MQListener(orderly=MQListener.ORDERLY_YES)
public class MatchMqConsumer4Seq {
	private static final Logger logger = LoggerFactory.getLogger(MatchMqConsumer4Seq.class);

	@Autowired
	private BBOrderService orderService;
	
	@Autowired
	private BBTradeService tradeService;

	@Autowired
	private BBMessageExtService msgService;
	
	@Autowired
	private BBMsgHandleThreadJob msgHandleThreadJob;

	//撮合未成交
	@MQListener(tags=MqTags.TAGS_NOT_MATCHED)
	public void handleNotMatch(BBNotMatchMsg msg){
		logger.info("收到撮合未成交消息:{}", msg);
		try{
			msgService.saveNotMatchedMsg(MqTags.TAGS_NOT_MATCHED, msg);
			msgHandleThreadJob.trigger();
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
		boolean ok = msgService.saveCancelIfNotExists(MqTags.TAGS_CANCELLED, msg, null);
		if(ok){
			msgHandleThreadJob.trigger();
		}else{
			logger.warn("订单取消msg已存在："+msg.getOrderId());
		}
	}
	
	//成交
	@MQListener(tags=MqTags.TAGS_TRADE)
	public void handleTradeMsg(BBTradeVo msg){
		logger.info("收到用户成交消息:{}", msg);
		try{
			msgService.saveTradeMsg(MqTags.TAGS_TRADE, msg, null);
			msgHandleThreadJob.trigger();
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
