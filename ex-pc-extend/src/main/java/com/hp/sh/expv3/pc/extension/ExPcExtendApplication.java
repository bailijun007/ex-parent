package com.hp.sh.expv3.pc.extension;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@ComponentScan("com.hp.sh.expv3")
@SpringBootApplication
public class ExPcExtendApplication {
	private static final Logger logger = LoggerFactory.getLogger(ExPcExtendApplication.class);

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ExPcExtendApplication.class, args);
	}
	
	@Value("${spring.profiles.active:}")
	private String profile;
	
	@PostConstruct
	private Object printEnv() {
		logger.warn("===========profile:{}============", profile);
		return null;
	}
}
