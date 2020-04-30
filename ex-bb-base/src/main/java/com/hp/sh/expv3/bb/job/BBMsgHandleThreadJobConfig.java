package com.hp.sh.expv3.bb.job;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.hp.sh.expv3.config.shard.ShardGroup;

@Configuration
public class BBMsgHandleThreadJobConfig{
    private static final Logger logger = LoggerFactory.getLogger(BBMsgHandleThreadJobConfig.class);

	@Autowired
	private MsgShardHandler msgShardHandler;
	
	@Autowired
	private ShardGroup shardGroup;
	
	private final Map<Long,MsgShardHandlerThread> threadMap = new HashMap<Long,MsgShardHandlerThread>();
	
	@Bean("__")
	int begin(){
		List<Long> list = shardGroup.getShardIdList();
		for(Long shardId : list){
			MsgShardHandlerThread thread = new MsgShardHandlerThread(msgShardHandler, shardId);
			threadMap.put(shardId, thread);
			thread.start();
		}
		return -1;
	}

	public void trigger(long shardId){
		MsgShardHandlerThread thread = threadMap.get(shardId);
		if(thread==null){
			logger.error("ShardThread=null,shardId={}",shardId);
			return;
		}
		thread.trigger();
	}
	
	@Scheduled(cron = "0/30 * * * * ?")
	public void timer() {
		for(MsgShardHandlerThread thread : threadMap.values()){
			thread.trigger();
		}
	}
	
}
