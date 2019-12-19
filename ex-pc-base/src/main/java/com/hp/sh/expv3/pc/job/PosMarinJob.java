package com.hp.sh.expv3.pc.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hp.sh.rocketmq.annotation.MQListener;

@Component
public class PosMarinJob {
    private static final Logger logger = LoggerFactory.getLogger(PosMarinJob.class);
    
	/**
	 * 
	 */
	@Scheduled(cron = "0 0/10 * * * ?")
	public void autoAddMargin() {
		
	}

	/**
	 * 自动增加保证金金
	 */
    @MQListener(group="group1", topic = "topic1")
	public void autoAddMargin(Object msg) {
		
	}

}
