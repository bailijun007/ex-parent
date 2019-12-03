package com.hp.sh.expv3.fund.cash.job.pay;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.fund.cash.entity.DepositRecord;
import com.hp.sh.expv3.fund.cash.service.complex.DepositService;

//@Component
@EnableScheduling
public class DepositePayJob {
    private static final Logger logger = LoggerFactory.getLogger(DepositePayJob.class);
    
    @Autowired
    private DepositService depositService;

	/**
	 * 处理已付款，未同步余额的记录
	 */
	@Scheduled(cron = "0 0/10 * * * ?")
	public void handlePendingSynch() {
		Page page = new Page(1, 10, 1000L);
		while(true){
			List<DepositRecord> list = this.depositService.findPendingPay(page);
			if(list==null || list.isEmpty()){
				break;
			}
			for(DepositRecord record : list){
				this.handleOne(record);
			}
		}
	}
	
	private void handleOne(DepositRecord record){
		boolean success = this.queryPayStatus(record.getSn());
		this.depositService.changePayStatus(record, DepositRecord.YES);
	}
	
	private boolean queryPayStatus(String sn){
		
		return false;
	}
	
}
