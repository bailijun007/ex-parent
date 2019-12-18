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
	 * 处理已付款，未同步余额的记录
	 */
	@Scheduled(cron = "0 0/10 * * * ?")
	public void handlePendingSynch() {
		
	}

	/**
	 * 处理已付款，未同步余额的记录
	 */
    @MQListener(group="group1", topic = "topic1")
	public void handleOnePendingSynch(Object msg) {
		
	}

}
