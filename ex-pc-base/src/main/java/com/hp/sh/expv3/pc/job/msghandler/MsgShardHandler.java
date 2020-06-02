package com.hp.sh.expv3.pc.job.msghandler;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.base.exceptions.CommonError;
import com.gitee.hupadev.base.exceptions.ExceptionUtils;
import com.gitee.hupadev.commons.bean.BeanHelper;
import com.gitee.hupadev.commons.json.JsonUtils;
import com.gitee.hupadev.commons.mybatis.ex.UpdateException;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.component.lock.impl.RedissonDistributedLocker;
import com.hp.sh.expv3.pc.constant.MqTags;
import com.hp.sh.expv3.pc.module.liq.service.PcLiqService;
import com.hp.sh.expv3.pc.module.msg.entity.PcMessageExt;
import com.hp.sh.expv3.pc.module.msg.service.PcMessageExtService;
import com.hp.sh.expv3.pc.module.order.service.PcOrderService;
import com.hp.sh.expv3.pc.module.position.service.PcTradeService;
import com.hp.sh.expv3.pc.mq.consumer.msg.PcNotMatchedMsg;
import com.hp.sh.expv3.pc.mq.consumer.msg.PcCancelledMsg;
import com.hp.sh.expv3.pc.mq.consumer.msg.PcTradeMsg;
import com.hp.sh.expv3.pc.mq.consumer.msg.liq.LiqCancelledMsg;

@Component
public class MsgShardHandler {
    private static final Logger logger = LoggerFactory.getLogger(MsgShardHandler.class);

	@Autowired
	private PcTradeService tradeService;
	
	@Autowired
	private PcOrderService orderService;
	
    @Autowired
    private PcLiqService pcLiqService;
	
	@Autowired
	private PcMessageExtService msgService;
	
	@Autowired
	private MsgShardHandler self;
	
	@Autowired
	private RedissonDistributedLocker locker;
	
	@Autowired
	private ErrorMsgCache errorMsgCache;

	private int batchNum = 600;
	
	public void handlePending(Long shardId) throws Exception {
		Lock lock = locker.getLock("msgHandlerSardJobLock-"+shardId, -1);
		boolean isLocked = lock.tryLock(0L, TimeUnit.SECONDS);
		if(isLocked){
			try{
				this.doHandlePending(shardId);
			}finally{
				lock.unlock();
			}
		}else{
			logger.warn("没有抢到ShardJob锁：{}", shardId);
		}
		
	}

	void doHandlePending(Long shardId) {
		while(true){
			List<PcMessageExt> shardMsgList = this.msgService.findFirstList(batchNum, shardId, null);
			if(shardMsgList==null || shardMsgList.isEmpty()){
				break;
			}
			logger.info("处理SHARD消息：shardId={},size={}", shardId, shardMsgList.size());
			Map<Long, List<PcMessageExt>> userMsgMap = BeanHelper.groupByProperty(shardMsgList, "userId");
			Set<Entry<Long, List<PcMessageExt>>> entrySet = userMsgMap.entrySet();
			
			for(Entry<Long, List<PcMessageExt>> entry : entrySet){
				Long userId = entry.getKey();
				List<PcMessageExt> userMsgList = entry.getValue();
				this.handleUserMsgList(shardId, userId, userMsgList);
			}
			
		}
	}
	
	private Long handleUserMsgList(Long shardId, Long userId, List<PcMessageExt> userMsgList){
		Long offsetId = null;
		try{
			long time1 = System.currentTimeMillis();
			logger.info("批量处理用户消息：shardId={}, threadName={}, userId={}, size={}", shardId, Thread.currentThread().getName(), userId, userMsgList.size());
			self.handleBatch(userId, userMsgList);
			offsetId = userMsgList.get(userMsgList.size()-1).getId();
			long time2 = System.currentTimeMillis();
			long time = time2 - time1;
			if(time>1000){
				logger.warn("批量处理用户消息成功：shardId={}, threadName={}, userId={}, size={}, time={}", shardId, Thread.currentThread().getName(), userId, userMsgList.size(), time);
			}else{
				logger.info("批量处理用户消息成功：shardId={}, threadName={}, userId={}, size={}, time={}", shardId, Thread.currentThread().getName(), userId, userMsgList.size(), time);
			}
		}catch(Exception e1){
			Exception cause = (Exception) ExceptionUtils.getCause(e1);
			logger.error("批量处理用户消息失败:{},{}", e1.getMessage(), cause.toString(), e1);
			if(isResendException(e1)){
				try {
					Thread.sleep(50);
				} catch (InterruptedException te) {
					logger.error(te.getMessage(), te);
				}
			}else{
				this.handleMsgAndErr(userId, userMsgList);
			}
		}
		return offsetId;
	}
	
	public void handleMsgAndErr(Long userId, List<PcMessageExt> userMsgList) {
		for(PcMessageExt msgExt: userMsgList){
			self.handleMsgAndErr(userId, msgExt);
		}
	}

	@LockIt(key="U-${userId}")
	@Transactional(rollbackFor=Exception.class)
	public void handleBatch(Long userId, List<PcMessageExt> userMsgList) {
		for(PcMessageExt msgExt : userMsgList){
			this.handleMsg(msgExt);
		}
	}

	@LockIt(key="U-${userId}")
	@Transactional(rollbackFor=Exception.class)
	public void handleMsgAndErr(Long userId, PcMessageExt msgExt){
		Long errMsgId = errorMsgCache.getErrorMsgIdCache(msgExt.getKeys());
		if(errMsgId!=null && msgExt.getId() > errMsgId){
			this.msgService.setStatus(msgExt.getUserId(), msgExt.getId(), PcMessageExt.STATUS_ERR, "前面存在未处理的消息:"+errMsgId);
		}else{
			logger.info("处理单个消息，shardId={}, msgId={}", msgExt.getShardId(), msgExt.getId());
			try{
				this.handleMsg(msgExt);
				if(errMsgId!=null){
					errorMsgCache.evictErrorMsgIdCache(msgExt.getKeys());
				}
				logger.info("处理单个消息成功，shardId={}, msgId={}", msgExt.getShardId(), msgExt.getId());
			}catch(Exception e){
				errorMsgCache.saveErrorMsgIdCache(msgExt.getKeys(), msgExt.getId());
				logger.error("处理单个消息失败，shardId={}, msgId={}", msgExt.getShardId(), msgExt.getId(), e);
				this.msgService.setStatus(msgExt.getUserId(), msgExt.getId(), PcMessageExt.STATUS_ERR, e.getMessage());
			}
			
		}
		
	}
	
	private void handleMsg(PcMessageExt msgExt){
		if(msgExt.getTags().equals(MqTags.TAGS_TRADE)){
			
			PcTradeMsg tradeMsg = JsonUtils.toObject(msgExt.getMsgBody(), PcTradeMsg.class);
			this.tradeService.handleTradeOrder(tradeMsg);
		}else if(msgExt.getTags().equals(MqTags.TAGS_CANCELLED)){
			
			PcCancelledMsg cancelMsg = JsonUtils.toObject(msgExt.getMsgBody(), PcCancelledMsg.class);
			this.orderService.setCancelled(cancelMsg.getAccountId(), cancelMsg.getAsset(), cancelMsg.getSymbol(), cancelMsg.getOrderId(), cancelMsg.getCancelNumber());
		}else if(msgExt.getTags().equals(MqTags.TAGS_NOT_MATCHED)){
			
			PcNotMatchedMsg cancelMsg = JsonUtils.toObject(msgExt.getMsgBody(), PcNotMatchedMsg.class);
			orderService.setNewStatus(cancelMsg.getAccountId(), cancelMsg.getAsset(), cancelMsg.getSymbol(), cancelMsg.getOrderId());
		}else if(msgExt.getTags().equals(MqTags.TAGS_ORDER_ALL_CANCELLED)){
			
			LiqCancelledMsg msg = JsonUtils.toObject(msgExt.getMsgBody(), LiqCancelledMsg.class);
			pcLiqService.cancelCloseOrder(msg.getAccountId(), msg.getAsset(), msg.getSymbol(), msg.getLongFlag(), msg.getPosId(), msg.getCancelOrders(), msg.getLastFlag(), msg.getLiqMarkPrice());
		}else{
			throw new RuntimeException("位置的tag类型!!! : " + msgExt.getTags());
		}
		msgService.delete(msgExt.getUserId(), msgExt.getId());
	}
	
	private boolean isResendException(Exception e){
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
