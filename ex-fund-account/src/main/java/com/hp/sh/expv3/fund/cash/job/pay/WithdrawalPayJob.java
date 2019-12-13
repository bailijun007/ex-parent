package com.hp.sh.expv3.fund.cash.job.pay;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.chainserver.client.WithDrawResponse;
import com.hp.sh.expv3.fund.cash.component.Asset2Symbol;
import com.hp.sh.expv3.fund.cash.component.ExChainService;
import com.hp.sh.expv3.fund.cash.entity.WithdrawalRecord;
import com.hp.sh.expv3.fund.cash.service.complex.WithdrawalService;

@Component
@EnableScheduling
public class WithdrawalPayJob {
    private static final Logger logger = LoggerFactory.getLogger(WithdrawalPayJob.class);
	
	@Autowired
	private ExChainService exChainService;
	
	@Autowired
	private Asset2Symbol asset2Symbol;

	@Autowired
	private WithdrawalService withdrawalService;

	/**
	 * 处理已批准的接口
	 */
	@Scheduled(cron = "0 0/10 * * * ?")
	public void handlePendingWithDrawal() {
		Page page = new Page(1, 10, 1000L);
		while(true){
			List<WithdrawalRecord> list = this.withdrawalService.findPendingWithDrawal(page);
			if(list==null || list.isEmpty()){
				break;
			}
			for(WithdrawalRecord record : list){
				this.handleOne(record);
			}
		}
	}
	
	private void handleOne(WithdrawalRecord record){
		logger.warn("待处理提现:{}", record.getId());
		//调用充值接口
		BigDecimal amount = record.getAmount();
		Integer symbol = asset2Symbol.getSymbol(record.getAsset());
		WithDrawResponse response = exChainService.draw(record.getUserId(), symbol , amount);
		if( response.getStatus()!=WithDrawResponse.STATUS_FAIL ){
			this.withdrawalService.onDrawSuccess(record.getUserId(), record.getId(), response.getTxHash());
		}else{
			this.withdrawalService.onDrawFail(record.getUserId(), record.getId());
		}
	}

}
