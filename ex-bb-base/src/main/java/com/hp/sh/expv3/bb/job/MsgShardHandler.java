package com.hp.sh.expv3.bb.job;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.base.exceptions.CommonError;
import com.gitee.hupadev.base.exceptions.ExceptionUtils;
import com.gitee.hupadev.commons.bean.BeanHelper;
import com.gitee.hupadev.commons.json.JsonUtils;
import com.gitee.hupadev.commons.mybatis.ex.UpdateException;
import com.hp.sh.expv3.bb.constant.MqTags;
import com.hp.sh.expv3.bb.module.msg.entity.BBMessageExt;
import com.hp.sh.expv3.bb.module.msg.service.BBMessageExtService;
import com.hp.sh.expv3.bb.module.msg.service.BBMessageOffsetService;
import com.hp.sh.expv3.bb.module.order.service.BBOrderService;
import com.hp.sh.expv3.bb.module.order.service.BBTradeService;
import com.hp.sh.expv3.bb.mq.msg.in.BBCancelledMsg;
import com.hp.sh.expv3.bb.mq.msg.in.BBNotMatchMsg;
import com.hp.sh.expv3.bb.strategy.vo.BBTradeVo;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.component.lock.impl.RedissonDistributedLocker;

@Component
public class MsgShardHandler {
    private static final Logger logger = LoggerFactory.getLogger(MsgShardHandler.class);

	@Autowired
	private BBTradeService tradeService;
	
	@Autowired
	private BBOrderService orderService;
	
	@Autowired
	private BBMessageExtService msgService;
	
	@Autowired
	private BBMessageOffsetService offsetService;
	
	@Autowired
	private MsgShardHandler self;
	
	@Autowired
	private RedissonDistributedLocker locker;

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
		Long offsetId = this.offsetService.getCachedShardOffset(shardId);
		while(true){
			List<BBMessageExt> shardMsgList = this.msgService.findFirstList(batchNum, shardId, offsetId);
			if(shardMsgList==null || shardMsgList.isEmpty()){
				break;
			}
			logger.info("处理SHARD消息：shardId={},size={}", shardId, shardMsgList.size());
			Map<Long, List<BBMessageExt>> userMsgMap = BeanHelper.groupByProperty(shardMsgList, "userId");
			Set<Entry<Long, List<BBMessageExt>>> entrySet = userMsgMap.entrySet();
			
			for(Entry<Long, List<BBMessageExt>> entry : entrySet){
				Long userId = entry.getKey();
				List<BBMessageExt> userMsgList = entry.getValue();
				while(true){
					try{
						long time = System.currentTimeMillis();
						logger.info("批量处理用户消息：shardId={}, threadName={}, userId={}, size={}", shardId, Thread.currentThread().getName(), userId, userMsgList.size());
						self.handleBatch(userId, userMsgList);
						offsetId = userMsgList.get(userMsgList.size()-1).getId();
						time = System.currentTimeMillis() - time;
						logger.info("批量处理用户消息成功：shardId={}, threadName={}, userId={}, size={}, time={}", shardId, Thread.currentThread().getName(), userId, userMsgList.size(), time);
						break;
					}catch(Exception e){
						Exception cause = (Exception) ExceptionUtils.getCause(e);
						logger.error("批量处理用户消息失败:{},{}", e.getMessage(), cause.toString(), e);
						if(isResendException(e)){
							try {
								Thread.sleep(10);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
							continue;
						}else{
							for(BBMessageExt msgExt : userMsgList){
								self.handleMsgAndErr(msgExt.getUserId(), msgExt);
								offsetId = msgExt.getId();
							}
							break;
						}
					}
				}
			}
			
		}
		this.offsetService.cacheShardOffset(shardId, offsetId);
	}
	
	@LockIt(key="U-${userId}")
	@Transactional(rollbackFor=Exception.class)
	public void handleBatch(Long userId, List<BBMessageExt> userMsgList) {
		for(BBMessageExt msgExt : userMsgList){
			this.handleMsg(msgExt);
		}
	}

	@LockIt(key="U-${userId}")
	@Transactional(rollbackFor=Exception.class)
	public void handleMsgAndErr(Long userId, BBMessageExt msgExt){
		Long errMsgId = self.getErrorMsgId(msgExt.getKeys());
		if(errMsgId!=null && msgExt.getId()>errMsgId){
			this.msgService.setStatus(msgExt.getUserId(), msgExt.getId(), BBMessageExt.STATUS_ERR, "前面存在未处理的消息:"+errMsgId);
		}else{
			logger.info("处理单个消息，shardId={}, msgId={}", msgExt.getShardId(), msgExt.getId());
			try{
				this.handleMsg(msgExt);
				logger.info("处理单个消息成功，shardId={}, msgId={}", msgExt.getShardId(), msgExt.getId());
			}catch(Exception e){
				self.saveErrorMsgId(msgExt.getKeys(), msgExt.getId());
				logger.info("处理单个消息失败，shardId={}, msgId={}", msgExt.getShardId(), msgExt.getId(), e);
				this.msgService.setStatus(msgExt.getUserId(), msgExt.getId(), BBMessageExt.STATUS_ERR, e.getMessage());
			}
			
		}
		
	}
	
	private void handleMsg(BBMessageExt msgExt){
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
	}
	
	@CachePut(cacheNames="errorMsg", key="#orderId")
	public Long saveErrorMsgId(Object orderId, Long msgId){
		return msgId;
	}

	@Cacheable(cacheNames="errorMsg", key="#orderId")
	public Long getErrorMsgId(Object orderId){
		return null;
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