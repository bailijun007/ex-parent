package com.hp.sh.expv3.config.job;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 
 * @author wangjg
 *
 */
@ConditionalOnProperty(name="xxl.job.enable", havingValue="false")
@EnableScheduling
@Configuration
public class ScheduleConfig {

	
}