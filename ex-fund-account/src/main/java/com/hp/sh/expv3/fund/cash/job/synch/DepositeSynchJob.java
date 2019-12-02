package com.hp.sh.expv3.fund.cash.job.synch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.fund.cash.entity.DepositRecord;
import com.hp.sh.expv3.fund.cash.service.complex.DepositService;

@Component
@EnableScheduling
public class DepositeSynchJob {
    private static final Logger logger = LoggerFactory.getLogger(DepositeSynchJob.class);
    
    @Autowired
    private DepositService depositService;

	/**
	 * 处理已付款，未同步余额的记录
	 */
	@Scheduled(cron = "0 0/10 * * * ?")
	public void handlePendingPay() {
		Page page = new Page(0, 10, 1000L);
		while(true){
			List<DepositRecord> list = this.depositService.findPendingSynch(page);
			if(list==null || list.isEmpty()){
				break;
			}
			for(DepositRecord record : list){
				this.handleOne(record);
			}
		}
	}
	
	private void handleOne(DepositRecord record){
		depositService.synchBalance(record);
	}
}
