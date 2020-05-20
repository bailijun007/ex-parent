package com.hp.sh.expv3.pc.extension.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.extension.sys.service.ShardTableService;
import com.hp.sh.rocketmq.annotation.MQListener;

@Component
@EnableScheduling
public class ShardTableMsgHandler {
    private static final Logger logger = LoggerFactory.getLogger(ShardTableMsgHandler.class);
    
	@Autowired
	private ShardTableService shardTableService;

	/**
	 * 分表
	 */
	@Scheduled(cron = "0 0 0 * * ?")
	@MQListener(topic = "update_shard_table")
	public void loadPhysicsTableNames() {
		shardTableService.loadPhysicsTableNames();
	}

}
