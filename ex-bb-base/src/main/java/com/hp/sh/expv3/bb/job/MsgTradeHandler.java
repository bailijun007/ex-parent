package com.hp.sh.expv3.bb.job;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.base.exceptions.CommonError;
import com.gitee.hupadev.base.exceptions.ExceptionUtils;
import com.gitee.hupadev.commons.json.JsonUtils;
import com.gitee.hupadev.commons.mybatis.ex.UpdateException;
import com.hp.sh.expv3.bb.constant.MqTags;
import com.hp.sh.expv3.bb.module.msg.entity.BBMessageExt;
import com.hp.sh.expv3.bb.module.msg.service.BBMessageExtService;
import com.hp.sh.expv3.bb.module.order.service.BBOrderService;
import com.hp.sh.expv3.bb.module.order.service.BBTradeService;
import com.hp.sh.expv3.bb.mq.msg.in.BBCancelledMsg;
import com.hp.sh.expv3.bb.mq.msg.in.BBNotMatchMsg;
import com.hp.sh.expv3.bb.mq.msg.in.BBTradeMsg;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.lock.LockIt;

@Service
public class MsgTradeHandler {
    private static final Logger logger = LoggerFactory.getLogger(MsgTradeHandler.class);

	@Autowired
	private BBTradeService tradeService;
	
	@Autowired
	private BBOrderService orderService;
	
	@Autowired
	private BBMessageExtService msgService;


	@LockIt(key="U-${userId}")
	@Transactional(rollbackFor=Exception.class)
	public void handleBatch(Long userId, List<BBMessageExt> userMsgList) {
		for(BBMessageExt msgExt : userMsgList){
			this.handleMsg(msgExt);
		}
	}
	
	@LockIt(key="U-${userId}")
	@Transactional(rollbackFor=Exception.class)
	public void handleOneMsg(BBMessageExt msgExt){
		this.handleMsg(msgExt);
	}
	
	private void handleMsg(BBMessageExt msgExt){
		if(msgExt.getTags().equals(MqTags.TAGS_TRADE)){
			BBTradeMsg tradeMsg = JsonUtils.toObject(msgExt.getMsgBody(), BBTradeMsg.class);
			this.tradeService.handleTrade(tradeMsg);
		}else if(msgExt.getTags().equals(MqTags.TAGS_CANCELLED)){
			BBCancelledMsg cancelMsg = JsonUtils.toObject(msgExt.getMsgBody(), BBCancelledMsg.class);
			this.orderService.setCancelled(cancelMsg.getAccountId(), cancelMsg.getAsset(), cancelMsg.getSymbol(), cancelMsg.getOrderId());
		}else if(msgExt.getTags().equals(MqTags.TAGS_NOT_MATCHED)){
			BBNotMatchMsg notMatched = JsonUtils.toObject(msgExt.getMsgBody(), BBNotMatchMsg.class);
			orderService.setNewStatus(notMatched.getAccountId(), notMatched.getAsset(), notMatched.getSymbol(), notMatched.getOrderId());
		}else{
			throw new RuntimeException("位置的tag类型!!! : " + msgExt.getTags());
		}
		msgService.delete(msgExt.getUserId(), msgExt.getId());
	}
	
	public static boolean isResendException(Exception e){
		Throwable cause = ExceptionUtils.getCause(e);
		if(cause instanceof UpdateException){
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
