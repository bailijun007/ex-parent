package com.hp.sh.expv3.fund;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableDiscoveryClient
@EnableFeignClients({"com.hp.sh.expv3.pc", "com.hp.sh.expv3.bb"})
@ComponentScan({"com.hp.sh.expv3"})
@SpringBootApplication
public class ExFundApplication {
	private static final Logger logger = LoggerFactory.getLogger(ExFundApplication.class);

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ExFundApplication.class, args);
	}
	
	@Value("${spring.profiles.active:}")
	private String profile;
	
	@PostConstruct
	private Object printEnv() {
		logger.warn("===========profile:{}============", profile);
		return null;
	}
}
