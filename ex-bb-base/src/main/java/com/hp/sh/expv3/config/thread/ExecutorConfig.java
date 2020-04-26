package com.hp.sh.expv3.config.thread;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gitee.hupadev.commons.executor.orderly.OrderlyExecutors;
import com.hp.sh.expv3.config.shard.ShardConstant;

@Configuration
public class ExecutorConfig {
	
	@Value("${excutor.maxThreads:20}")
	private Integer maxThreads;
	
	@Bean("tradeExecutors")
	public OrderlyExecutors tradeExecutors(){
		return new OrderlyExecutors(ShardConstant.SHARD_NUM, 10000);
	}

}
