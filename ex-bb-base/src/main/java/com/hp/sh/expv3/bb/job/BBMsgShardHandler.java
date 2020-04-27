package com.hp.sh.expv3.bb.job;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.bean.BeanHelper;
import com.gitee.hupadev.commons.executor.orderly.OrderlyExecutors;
import com.gitee.hupadev.commons.json.JsonUtils;
import com.hp.sh.expv3.bb.constant.MqTags;
import com.hp.sh.expv3.bb.module.msg.entity.BBMessageExt;
import com.hp.sh.expv3.bb.module.msg.service.BBMessageExtService;
import com.hp.sh.expv3.bb.module.msg.service.BBMessageOffsetService;
import com.hp.sh.expv3.bb.module.order.service.BBOrderService;
import com.hp.sh.expv3.bb.module.order.service.BBTradeService;
import com.hp.sh.expv3.bb.mq.msg.in.BBCancelledMsg;
import com.hp.sh.expv3.bb.mq.msg.in.BBNotMatchMsg;
import com.hp.sh.expv3.bb.strategy.vo.BBTradeVo;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.component.executor.AbstractGroupTask;
import com.hp.sh.expv3.dev.LimitTimeHandle;

//@Component
public class BBMsgShardHandler {
    private static final Logger logger = LoggerFactory.getLogger(BBMsgShardHandler.class);

	@Autowired
	private BBTradeService tradeService;
	
	@Autowired
	private BBOrderService orderService;
	
	@Autowired
	private BBMessageExtService msgService;
	
	@Autowired
	private BBMessageOffsetService offsetService;
	
	private Map<Object,Long> errorMsgMap = new ConcurrentHashMap<Object,Long>();
	
	@Autowired
	private BBMsgShardHandler self;

	private int batchNum = 1000;

	@LockIt(key="shard-${shardId}")
	public void handlePending(int shardId) {
		Long offsetId = this.offsetService.getCachedShardOffset(shardId);
		while(true){
			List<BBMessageExt> list = this.msgService.findFirstList(batchNum, shardId, offsetId);
			if(list==null || list.isEmpty()){
				break;
			}
			
			Map<Long, List<BBMessageExt>> userMsgMap = BeanHelper.groupByProperty(list, "userId");
			Set<Entry<Long, List<BBMessageExt>>> entrySet = userMsgMap.entrySet();
			
			for(Entry<Long, List<BBMessageExt>> entry : entrySet){
				Long userId = entry.getKey();
				List<BBMessageExt> userMsgList = entry.getValue();
				try{
					self.handleBatch(userMsgList);
					offsetId = userMsgList.get(userMsgList.size()-1).getId();
				}catch(Exception e){
					for(BBMessageExt msgExt : userMsgList){
						self.handleMsgAndErr(msgExt);
						offsetId = msgExt.getId();
					}
				}
			}
			
		}
		this.offsetService.cacheShardOffset(shardId, offsetId);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void handleBatch(List<BBMessageExt> userMsgList) {
		for(BBMessageExt msgExt : userMsgList){
			self.handleMsgAndErr(msgExt);
		}
	}

	public void handleMsgAndErr(BBMessageExt msgExt){
		Long errMsgId = errorMsgMap.get(msgExt.getKeys());
		if(errMsgId!=null && msgExt.getId()>errMsgId){
			this.msgService.setStatus(msgExt.getUserId(), msgExt.getId(), BBMessageExt.STATUS_ERR, "前面存在未处理的消息:"+errMsgId);
		}else{
			this.handleMsg(msgExt);
		}
		
	}
	
	private void handleMsg(BBMessageExt msgExt){
		logger.info("task处理消息，{}", msgExt.getId());
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
			msgService.delete(msgExt.getUserId(), msgExt.getId());
		}catch(Exception e){
			errorMsgMap.put(msgExt.getKeys(), msgExt.getId());
			logger.error("[异步]处理消息失败 {},{}", e.getMessage(), e.toString(), e);
			this.msgService.setStatus(msgExt.getUserId(), msgExt.getId(), BBMessageExt.STATUS_ERR, e.getMessage());
		}
	}
	
}
