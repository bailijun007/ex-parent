package com.hp.sh.expv3.bb.trade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@EnableScheduling
@EnableDiscoveryClient
@ComponentScan("com.hp.sh.expv3")
@SpringBootApplication
public class ExBBTradeApplication {
    private static final Logger logger = LoggerFactory.getLogger(ExBBTradeApplication.class);

    public static void main(String[] args) throws Exception {
        final ConfigurableApplicationContext ac = SpringApplication.run(ExBBTradeApplication.class, args);

//        final BbKlineHistoryCalcByTradeFromExpService bbKlineHistoryCalcByTradeFromExpService = ac.getBean(BbKlineHistoryCalcByTradeFromExpService.class);
//        bbKlineHistoryCalcByTradeFromExpService.execute();

    }

    @Value("${spring.profiles.active:}")
    private String profile;

    @PostConstruct
    private Object printEnv() {
        logger.warn("===========profile:{}============", profile);
        return null;
    }
}
