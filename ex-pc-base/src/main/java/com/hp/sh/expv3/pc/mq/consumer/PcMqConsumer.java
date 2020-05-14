package com.hp.sh.expv3.pc.mq.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.base.exceptions.CommonError;
import com.gitee.hupadev.base.exceptions.ExceptionUtils;
import com.gitee.hupadev.commons.mybatis.ex.UpdateException;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.constant.MqTags;
import com.hp.sh.expv3.pc.module.liq.service.PcLiqService;
import com.hp.sh.expv3.pc.module.order.service.PcOrderService;
import com.hp.sh.expv3.pc.module.position.service.PcTradeService;
import com.hp.sh.expv3.pc.mq.consumer.msg.PcNotMatchedMsg;
import com.hp.sh.expv3.pc.mq.consumer.msg.PcCancelledMsg;
import com.hp.sh.expv3.pc.mq.consumer.msg.PcTradeMsg;
import com.hp.sh.expv3.pc.mq.consumer.msg.liq.LiqCancelledMsg;
import com.hp.sh.rocketmq.annotation.MQListener;

@Component
@MQListener(orderly=MQListener.ORDERLY_YES)
@ConditionalOnProperty(name="mq.orderly.consumer.select", havingValue="1")
public class PcMqConsumer {
	private static final Logger logger = LoggerFactory.getLogger(PcMqConsumer.class);

	@Autowired
	private PcOrderService pcOrderService;
	
	@Autowired
	private PcTradeService pcTradeService;
	
    @Autowired
    private PcLiqService pcLiqService;

	@MQListener(tags=MqTags.TAGS_NOT_MATCHED)
	public void handleNotMatch(PcNotMatchedMsg msg){
		logger.info("收到撮合未成消息:{}", msg);
		pcOrderService.setNewStatus(msg.getAccountId(), msg.getAsset(), msg.getSymbol(), msg.getOrderId());
	}
	
	//取消订单
	@MQListener(tags=MqTags.TAGS_CANCELLED)
	public void handleCancelledMsg(PcCancelledMsg msg){
		logger.info("收到取消订单消息:{}", msg);
		this.pcOrderService.setCancelled(msg.getAccountId(), msg.getAsset(), msg.getSymbol(), msg.getOrderId(), msg.getCancelNumber());
	}
	
	//成交
	@MQListener(tags=MqTags.TAGS_TRADE)
	public void handleTradeMsg(PcTradeMsg msg){
		logger.info("收到成交消息:{}", msg);
		pcTradeService.handleTradeOrder(msg);
	}
	
	@MQListener(tags = MqTags.TAGS_ORDER_ALL_CANCELLED)
	public void handleCancelled(LiqCancelledMsg msg){
		logger.warn("收到强平撤销消息:{}", msg);
		pcLiqService.cancelCloseOrder(msg.getAccountId(), msg.getAsset(), msg.getSymbol(), msg.getLongFlag(), msg.getPosId(), msg.getCancelOrders(), msg.getLastFlag(), msg.getLiqMarkPrice());
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
