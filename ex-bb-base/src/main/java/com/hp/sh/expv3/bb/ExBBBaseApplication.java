package com.hp.sh.expv3.bb;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

import com.hp.sh.expv3.error.ExCommonError;

@EnableDiscoveryClient
@ComponentScan("com.hp.sh.expv3")
@SpringBootApplication
public class ExBBBaseApplication {
	private static final Logger logger = LoggerFactory.getLogger(ExBBBaseApplication.class);

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ExBBBaseApplication.class, args);
	}
	
	@Value("${spring.profiles.active:}")
	private String profile;
	
	@PostConstruct
	private Object printEnv() {
		logger.warn("===========profile:{}============", profile);
		try{
			logger.warn("{}",ExCommonError.REPEAT_ORDER.getMessage());
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		return null;
	}
}
