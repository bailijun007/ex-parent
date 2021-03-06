package com.hp.sh.expv3.bb.extension;

import javax.annotation.PostConstruct;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@MapperScan({"com.hp.sh.expv3.bb.**.dao"})
@ComponentScan("com.hp.sh.expv3")
@SpringBootApplication
@EnableScheduling
public class ExBBExtendApplication {
	private static final Logger logger = LoggerFactory.getLogger(ExBBExtendApplication.class);

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ExBBExtendApplication.class, args);
	}
	
	@Value("${spring.profiles.active:}")
	private String profile;
	
	@PostConstruct
	private Object printEnv() {
		logger.warn("===========profile:{}============", profile);
		return null;
	}
}
