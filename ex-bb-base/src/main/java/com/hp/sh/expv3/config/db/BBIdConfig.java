package com.hp.sh.expv3.config.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.hp.sh.expv3.component.id.ZwIdGenerator;
import com.hp.sh.expv3.component.id.config.WorkerConfig;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class BBIdConfig {
	
	@Autowired
	private WorkerConfigBuilder workerConfigBuilder;

	@Bean
	public ZwIdGenerator zwIdGenerator() {
		WorkerConfig workerConfig = workerConfigBuilder.getWorkerConfig();
		ZwIdGenerator zwIdGenerator = new ZwIdGenerator(workerConfig);
		return zwIdGenerator;
	}

}
