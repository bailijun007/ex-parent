package com.hp.sh.expv3.fund.cash.job.synch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.fund.cash.entity.WithdrawalRecord;
import com.hp.sh.expv3.fund.cash.service.complex.WithdrawalService;

@Component
@EnableScheduling
public class WithdrawalSynchJob {
    private static final Logger logger = LoggerFactory.getLogger(WithdrawalSynchJob.class);
    
    @Autowired
    private WithdrawalService withdrawalService;

	/**
	 * 处理已付款，未同步余额的记录
	 */
	@Scheduled(cron = "0 0/10 * * * ?")
	public void handlePendingPay() {
		Page page = new Page(0, 10, 1000L);
		while(true){
			List<WithdrawalRecord> list = this.withdrawalService.findPendingSynch(page);
			if(list==null || list.isEmpty()){
				break;
			}
			for(WithdrawalRecord record : list){
				this.handleOne(record);
			}
		}
	}
	
	private void handleOne(WithdrawalRecord record){
		withdrawalService.synchReturnBalance(record);
	}
}
