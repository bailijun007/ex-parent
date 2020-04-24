package com.hp.sh.expv3.bb.job;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.executor.orderly.OrderlyExecutors;
import com.gitee.hupadev.commons.json.JsonUtils;
import com.hp.sh.expv3.bb.constant.MqTags;
import com.hp.sh.expv3.bb.module.msg.entity.BBMessageExt;
import com.hp.sh.expv3.bb.module.msg.service.BBMessageExtService;
import com.hp.sh.expv3.bb.module.order.service.BBOrderService;
import com.hp.sh.expv3.bb.module.order.service.BBTradeService;
import com.hp.sh.expv3.bb.mq.msg.in.BBCancelledMsg;
import com.hp.sh.expv3.bb.mq.msg.in.BBNotMatchMsg;
import com.hp.sh.expv3.bb.strategy.vo.BBTradeVo;
import com.hp.sh.expv3.component.executor.AbstractGroupTask;
import com.hp.sh.expv3.dev.LimitTimeHandle;

@Component
public class BBMsgHandler {
    private static final Logger logger = LoggerFactory.getLogger(BBMsgHandler.class);

	@Autowired
	private BBTradeService tradeService;
	
	@Autowired
	private BBOrderService orderService;
	
	@Autowired
	private OrderlyExecutors tradeExecutors;

	@Autowired
	private BBMessageExtService msgService;
	
	private Map<Long,String> userErrorMap = new ConcurrentHashMap<Long,String>();

	private int batchNum = 50;
	
	@LimitTimeHandle
	public void handlePending() {
		while(true){
			List<BBMessageExt> list = this.msgService.findFirstList(batchNum);
			
			if(list==null || list.isEmpty()){
				break;
			}
			
			for(BBMessageExt msg : list){
				tradeExecutors.submit(new MsgTask(msg));
				this.msgService.setStatus(msg.getUserId(), msg.getMsgId(), BBMessageExt.STATUS_RUNNING, "处理中");
			}
			
		}
		
	}
	
	private void handleOneMsg(BBMessageExt msgExt){
		if(userErrorMap.containsKey(msgExt.getUserId())){
			logger.error("用户存在出错未处理的消息{}", msgExt.getUserId());
			msgService.setStatus(msgExt.getUserId(), msgExt.getMsgId(), BBMessageExt.STATUS_ERR, "用户存在出错未处理的消息:"+userErrorMap.get(msgExt.getUserId()));
			return;
		}
		try{
			if(msgExt.getTags().equals(MqTags.TAGS_TRADE)){
				BBTradeVo tradeMsg = JsonUtils.toObject(msgExt.getMsgBody(), BBTradeVo.class);
				this.tradeService.handleTrade(tradeMsg);
			}else if(msgExt.getTags().equals(MqTags.TAGS_CANCELLED)){
				BBCancelledMsg cancelMsg = JsonUtils.toObject(msgExt.getMsgBody(), BBCancelledMsg.class);
				this.orderService.setCancelled(cancelMsg.getAccountId(), cancelMsg.getAsset(), cancelMsg.getSymbol(), cancelMsg.getOrderId());
			}else if(msgExt.getTags().equals(MqTags.TAGS_NOT_MATCHED)){
				BBNotMatchMsg cancelMsg = JsonUtils.toObject(msgExt.getMsgBody(), BBNotMatchMsg.class);
				orderService.setNewStatus(cancelMsg.getAccountId(), cancelMsg.getAsset(), cancelMsg.getSymbol(), cancelMsg.getOrderId());
			}else{
				throw new RuntimeException("位置的tag类型!!! : " + msgExt.getTags());
			}
			msgService.delete(msgExt.getUserId(), msgExt.getMsgId());
		}catch(Exception e){
			logger.error("[异步]处理消息失败 "+e.getMessage(), e);
			userErrorMap.put(msgExt.getUserId(), msgExt.getMsgId());
			msgService.setStatus(msgExt.getUserId(), msgExt.getMsgId(), BBMessageExt.STATUS_ERR, e.getMessage());
		}
	}
	
	class MsgTask extends AbstractGroupTask{

		private BBMessageExt msg;
		
		public MsgTask(BBMessageExt msg) {
			this.msg = msg;
		}
		
		public int getKey(){
			int key = msg.getUserId().hashCode();
			return Math.abs(key);
		}

		public void doRun() {
			handleOneMsg(msg);
		}

	}
	
}
