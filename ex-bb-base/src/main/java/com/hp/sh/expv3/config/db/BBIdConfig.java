package com.hp.sh.expv3.config.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hp.sh.expv3.component.id.ZwIdGenerator;
import com.hp.sh.expv3.component.id.config.WorkerConfig;

@Configuration
public class BBIdConfig {

	@Value("${id.generator.dataCenterId}")
	private int dataCenterId;
	
	@Value("${id.generator.serverId}")
	private int serverId;
	
	@Autowired
	private WorkerConfigBuilder workerConfigBuilder;

	@Bean
	public ZwIdGenerator zwIdGenerator() {
		WorkerConfig workerConfig = workerConfigBuilder.getWorkerConfig();
		ZwIdGenerator zwIdGenerator = new ZwIdGenerator(workerConfig);
		return zwIdGenerator;
	}

}
