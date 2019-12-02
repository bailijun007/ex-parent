package com.hp.sh.expv3.fund.transfer.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.fund.transfer.api.FundTransferCoreApi;
import com.hp.sh.expv3.fund.transfer.constant.MQConstant;
import com.hp.sh.rocketmq.annotation.MQListener;

@Component
@EnableScheduling
public class TransferJob {
    private static final Logger logger = LoggerFactory.getLogger(TransferJob.class);
    
    @Autowired
    private FundTransferCoreApi fundTransferCoreApi;

	/**
	 * 处理已付款，未同步余额的记录
	 */
    @MQListener(topic = MQConstant.TOPIC)
	@Scheduled(cron = "0 0/10 * * * ?")
	public void handlePendingPay() {
		fundTransferCoreApi.handlePending();
	}

}
