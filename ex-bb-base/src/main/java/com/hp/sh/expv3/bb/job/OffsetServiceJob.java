package com.hp.sh.expv3.bb.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.module.msg.service.BBMessageOffsetService;
import com.hp.sh.expv3.config.shard.ShardConstant;

@Order(Ordered.LOWEST_PRECEDENCE)
@Component
public class OffsetServiceJob {
    private static final Logger logger = LoggerFactory.getLogger(OffsetServiceJob.class);

	@Autowired
	private BBMessageOffsetService offsetService;
	
	@Scheduled(cron = "0/30 * * * * ?")
	public void handle() {
		int num = ShardConstant.SHARD_NUM;
		for(int shardId=0; shardId < num; shardId++){
			offsetService.persistCachedOffset(shardId);
		}
	}

}
