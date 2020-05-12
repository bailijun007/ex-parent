package com.hp.sh.expv3.bb.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.module.sys.service.ShardTableService;

@Order(Ordered.LOWEST_PRECEDENCE)
@Component
public class CreateTableJob {
    private static final Logger logger = LoggerFactory.getLogger(CreateTableJob.class);

	@Autowired
	private ShardTableService shardTableService;
	
	@Scheduled(cron = "0 0 11 20 * ?")
	public void handle() {
		shardTableService.createNextMonthTables();
	}

}
