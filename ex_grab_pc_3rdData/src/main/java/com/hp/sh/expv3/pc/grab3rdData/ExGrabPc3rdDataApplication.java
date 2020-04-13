package com.hp.sh.expv3.pc.grab3rdData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

/**
 * @author BaiLiJun  on 2020/4/13
 */
@EnableScheduling
@EnableDiscoveryClient
@ComponentScan("com.hp.sh.expv3")
@SpringBootApplication
public class ExGrabPc3rdDataApplication {
    private static final Logger logger = LoggerFactory.getLogger(ExGrabPc3rdDataApplication.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ExGrabPc3rdDataApplication.class, args);
    }

    @Value("${spring.profiles.active:}")
    private String profile;

    @PostConstruct
    private Object printEnv() {
        logger.warn("===========profile:{}============", profile);
        return null;
    }
}
