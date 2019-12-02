package com.hp.sh.expv3.config.db.job;

import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Component
@EnableScheduling
public class CreateTableJob {
    private static final Logger logger = LoggerFactory.getLogger(CreateTableJob.class);
    
    @Autowired
    private SqlSessionFactory sf;
    
    private List<String> list;

	/**
	 * 处理已付款，未同步余额的记录
	 */
    @GetMapping(value = "/api/db/createTable")
	@Scheduled(cron = "0 0 0 * * ?")
	public void createTable() {
		
	}

}
