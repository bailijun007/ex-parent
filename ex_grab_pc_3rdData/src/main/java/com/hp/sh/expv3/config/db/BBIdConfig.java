package com.hp.sh.expv3.config.db;

import com.hp.sh.expv3.component.id.ZwIdGenerator;
import com.hp.sh.expv3.component.id.config.WorkerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
