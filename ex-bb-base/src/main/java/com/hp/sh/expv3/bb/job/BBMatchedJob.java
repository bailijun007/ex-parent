package com.hp.sh.expv3.bb.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//禁用定时任务，靠mq排序无法使用job
//@Component
public class BBMatchedJob {
    private static final Logger logger = LoggerFactory.getLogger(BBMatchedJob.class);

	@Autowired
	private BBMatchedHandler matchedHandler;
	
	@Scheduled(cron = "0 0/10 * * * ?")
	public void handleJob() {
		matchedHandler.handlePending();
	}
	
}
