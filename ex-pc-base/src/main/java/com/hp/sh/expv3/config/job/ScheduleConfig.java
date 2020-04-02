package com.hp.sh.expv3.config.job;

import javax.annotation.PostConstruct;

import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 
 * @author wangjg
 *
 */
@ConditionalOnProperty(name="job.type", havingValue="schedule")
@EnableScheduling
@Configuration
public class ScheduleConfig {
    private Logger logger = LoggerFactory.getLogger(ScheduleConfig.class);
    
	@PostConstruct
	public void init() throws MQClientException{
		logger.info(">>>>>>>>>>> Scheduled config init.");
	}
	
}
