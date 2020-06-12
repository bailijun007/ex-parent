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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.bean.BeanHelper;
import com.hp.sh.expv3.bb.module.msg.entity.BBMessageExt;
import com.hp.sh.expv3.bb.module.msg.service.BBMessageExtService;
import com.hp.sh.expv3.component.lock.impl.RedissonDistributedLocker;

@Component
public class MsgShardHandler {
    private static final Logger logger = LoggerFactory.getLogger(MsgShardHandler.class);
	
	@Autowired
	private BBMessageExtService msgService;
	
	@Autowired
	private RedissonDistributedLocker locker;
	
	@Autowired
	private MsgTradeHandler msgTradeHandler;
	
	@Value("${msg.handler.batch.enable:true}")
	private Boolean batch;

	private int queryBatchCount = 3000;

	@Value("${msg.handler.batch.count:20}")
	private int handleBatchCount;
	
	public void handlePending(Long shardId) throws Exception {
		Lock lock = locker.getLock("msgHandlerSardJobLock-"+shardId, -1);
		boolean isLocked = lock.tryLock(0L, TimeUnit.SECONDS);
		if(isLocked){
			try{
				if(this.batch){
					this.handleMsgGroupByUserId(shardId);
				}else{
					this.handleEachMsg(shardId);
				}
			}finally{
				lock.unlock();
			}
		}else{
			logger.warn("没有抢到ShardJob锁：{}", shardId);
		}
		
	}

	private void handleMsgGroupByUserId(Long shardId) {
		while(true){
			List<BBMessageExt> shardMsgList = this.msgService.findFirstList(queryBatchCount, shardId, null, null);
			if(shardMsgList==null || shardMsgList.isEmpty()){
				break;
			}
			
			long shardTime = System.currentTimeMillis();
			logger.info("处理SHARD消息：shardId={},size={}, threadId={}", shardId, shardMsgList.size(), Thread.currentThread().getId());
			
			Map<Long, List<BBMessageExt>> userMsgMap = BeanHelper.groupByProperty(shardMsgList, "userId");
			Set<Entry<Long, List<BBMessageExt>>> entrySet = userMsgMap.entrySet();
			
			for(Entry<Long, List<BBMessageExt>> entry : entrySet){
				Long userId = entry.getKey();
				List<BBMessageExt> userMsgList = entry.getValue();

				List<List<BBMessageExt>> subMsgLists = BeanHelper.splitList(userMsgList, handleBatchCount);
				
				for(List<BBMessageExt> subMsgList: subMsgLists){
					logger.info("批量处理用户消息开始：userId={}, size={}", userId, subMsgList.size());
					
					long time = System.currentTimeMillis();
					try{
						
						msgTradeHandler.handleBatch(userId, subMsgList);
						
						time = System.currentTimeMillis() - time;
						
						logger.info("批量处理用户消息结束：shardId={}, userId={}, size={}, time={}", shardId, userId, subMsgList.size(), time);
						
						if(time > 3000){
							logger.warn("批量处理用户消息结束：shardId={}, userId={}, size={}, time={}", shardId, userId, subMsgList.size(), time);
						}
						
					}catch(Exception e){
						time = System.currentTimeMillis() - time;
						logger.error("批量处理用户消息失败！shardId={}, userId={}, size={}, time={}", shardId, userId, subMsgList.size(), time);
						//如果失败不影响其他用户的消息处理，因为不同用户不需要顺序处理。失败的等待下一次循环处理。。。
					}
				}
				
			}
			
			shardTime = System.currentTimeMillis() - shardTime;
			logger.info("处理SHARD消息结束：shardId={}, size={}, threadId={}, time={}", shardId, shardMsgList.size(), Thread.currentThread().getId(), shardTime);
			
		}
	}
	
	@SuppressWarnings("unused")
	private void handleEachMsg(Long shardId) {
		while(true){
			List<BBMessageExt> shardMsgList = this.msgService.findFirstList(queryBatchCount, shardId, null, null);
			if(shardMsgList==null || shardMsgList.isEmpty()){
				break;
			}
			
			
			for(BBMessageExt msg: shardMsgList){
				logger.info("单个处理用户消息开始：msgId={}", msg.getId());
				
				long time = System.currentTimeMillis();
				try{
					
					msgTradeHandler.handleOneMsg(msg);
					
					time = System.currentTimeMillis() - time;
					
					logger.info("单个处理用户消息结束：msgId={}, time={}", msg.getId(), time);
					
					if(time > 1000){
						logger.error("单个处理用户消息结束：msgId={}, time={}", msg.getId(), time);
					}
					
				}catch(Exception e){
					time = System.currentTimeMillis() - time;
					logger.error("单个处理用户消息失败：msgId={}, time={}", msg.getId(), time);
					//如果失败不影响其他用户的消息处理，因为不同用户不需要顺序处理。失败的等待下一次循环处理。。。
				}
			}
			
			
		}
	}

}
