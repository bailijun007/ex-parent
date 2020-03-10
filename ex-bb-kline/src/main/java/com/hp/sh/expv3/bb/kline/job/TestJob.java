package com.hp.sh.expv3.bb.kline.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TestJob {
    private static final Logger logger = LoggerFactory.getLogger(TestJob.class);

	
	@Scheduled(cron = "0 0/10 * * * ?")
	public void handleJob() {
		logger.debug("test...");
	}
	
}
