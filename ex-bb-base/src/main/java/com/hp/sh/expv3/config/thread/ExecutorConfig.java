package com.hp.sh.expv3.config.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gitee.hupadev.commons.executor.orderly.OrderlyExecutors;

@Configuration
public class ExecutorConfig {
	
	@Bean("tradeExecutors")
	public OrderlyExecutors tradeExecutors(){
		return new OrderlyExecutors(20, 500);
	}

}
