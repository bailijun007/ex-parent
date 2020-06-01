package com.hp.sh.expv3.pc.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.module.sys.service.ShardTableService;

@Order(Ordered.LOWEST_PRECEDENCE)
@Component
public class CreateTableJob {
    private static final Logger logger = LoggerFactory.getLogger(CreateTableJob.class);

	@Autowired
	private ShardTableService shardTableService;
	
	@Scheduled(cron = "0 0 11 25 * ?")
	public void handle1() {
		shardTableService.createNextMonthTables();
	}
	
	@Scheduled(cron = "0 0 11 28 * ?")
	public void handle2() {
		shardTableService.createNextMonthTables();
	}

}
