package com.hp.sh.expv3.bb.job;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderTrade;
import com.hp.sh.expv3.bb.module.order.service.BBOrderQueryService;
import com.hp.sh.expv3.bb.module.order.service.BBTradeService;
import com.hp.sh.expv3.utils.DbDateUtils;

@Component
public class BBOrderTradeFeeJob {
    private static final Logger logger = LoggerFactory.getLogger(BBOrderTradeFeeJob.class);

	@Autowired
	private BBOrderQueryService orderQueryService;
	@Autowired
	private BBTradeService tradeService;
	
//	@Scheduled(cron = "0 0/1 * * * ?")
	public void handleJob() {
		Long now = DbDateUtils.now();
		Long startTime = now-1000*3600*20000;
		Page page = new Page(1, 100, 1000L);
		while(true){
			List<BBOrderTrade> list = orderQueryService.querySynchFee(page, startTime);
			
			if(list==null || list.isEmpty()){
				break;
			}
			
			for(BBOrderTrade orderTrade : list){
				handleOrderTrade(orderTrade);
				
			}
			
		}
	}

	private void handleOrderTrade(BBOrderTrade orderTrade) {
		tradeService.synchCollector(orderTrade);
		tradeService.setSynchStatus(orderTrade);
	}
	
}
