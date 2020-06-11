package com.hp.sh.expv3;

import com.hp.sh.expv3.controller.NewAssetDefaultKlineDataController;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

/**
 * @author BaiLiJun  on 2020/5/6
 */
@SpringBootApplication
@EnableDiscoveryClient
public class KlineDataByThirdData {

    private static final Logger logger = LoggerFactory.getLogger(KlineDataByThirdData.class);

    public static void main(String[] args) {
        SpringApplication.run(KlineDataByThirdData.class, args);
    }

    @Value("${spring.profiles.active:}")
    private String profile;

    @Autowired
    private NewAssetDefaultKlineDataController assetDefaultKlineDataController;

    @PostConstruct
    private Object printEnv() {
        logger.warn("===========profile:{}============", profile);
        return null;
    }

//    @PostConstruct
    private void start(){
        assetDefaultKlineDataController.getDefaultKlineData(null,null,null,null);
    }
}
